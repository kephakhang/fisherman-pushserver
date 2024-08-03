@file:Suppress("NAME_SHADOWING")

package kr.co.korbit.fisherman.pushserver

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.default
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.header
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.ShutDownUrl
import io.ktor.util.KtorExperimentalAPI
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kr.co.korbit.fisherman.common.BackgroundJob
import kr.co.korbit.fisherman.common.BigDecimalSerializer
import kr.co.korbit.fisherman.common.TimestampSerializer
import kr.co.korbit.fisherman.exception.InvalidMessageException
import kr.co.korbit.fisherman.exception.stackTraceString
import kr.co.korbit.fisherman.pushserver.env.Env
import kr.co.korbit.fisherman.pushserver.env.Env.Companion.sha256
import kr.co.korbit.fisherman.pushserver.env.clientIp
import kr.co.korbit.fisherman.pushserver.kafka.KafkaEventService
import kr.co.korbit.fisherman.pushserver.kafka.buildConsumer
import kr.co.korbit.fisherman.pushserver.kafka.buildProducer
import kr.co.korbit.fisherman.pushserver.model.*
import kr.co.korbit.fisherman.pushserver.service.AuthService
import kr.co.korbit.wsserver.PushServer
import mu.KotlinLogging
import org.slf4j.event.Level
import java.io.File
import java.io.FilenameFilter
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.thread
import kotlin.concurrent.timerTask


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

// embeddeServer(Netty) 설정 관련 필드
//open class Configuration {
//    /**
//     * Provides currently available parallelism, e.g. number of available processors
//     */
//    val parallelism: Int = Runtime.getRuntime().availableProcessors()
//
//    /**
//     * Specifies size of the event group for accepting connections
//     */
//    var connectionGroupSize: Int = parallelism / 2 + 1
//
//    /**
//     * Specifies size of the event group for processing connections, parsing messages and doing engine's internal work
//     */
//    var workerGroupSize: Int = parallelism / 2 + 1
//
//    /**
//     * Specifies size of the event group for running application code
//     */
//    var callGroupSize: Int = parallelism
//}


private val logger = KotlinLogging.logger {}

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    //val random = Random(Date().time)
    val gson = GsonBuilder().registerTypeAdapter(Timestamp::class.java, TimestampSerializer())
        .registerTypeAdapter(BigDecimal::class.java, BigDecimalSerializer()).create()
    val auth = AuthService()
    //val pushServerMap = ConcurrentHashMap<String, PushServer<User>>()

    var applicable = environment.config.config("ktor.deployment").property("applicable").toString()
    if (System.getenv("APPLICABLE") != null) {
        applicable = System.getenv("APPLICABLE")
    }
    if (testing) {
        return
    }

    Env.branch = environment.config.config("ktor.deployment").property("branch").toString()

    Env.topicLogging = environment.config.config("ktor.kafka.consumer").property("logging").getString().toBoolean()

    //MGK_DEL : currency 테스트 용
//    try {
//        val coininfoPath = Paths.get("").toAbsolutePath().toString() + "/config/coininfo.json"
//        val mapper = jacksonObjectMapper()
//        val json = File(coininfoPath).readText(Charsets.UTF_8)
//        val coinType = object : TypeToken<MutableList<Coin>>() {}.type
//        Env.currencyList = java.util.Collections.synchronizedList(gson.fromJson<MutableList<Coin>>(json, coinType))
//        for(currency in Env.currencyList as MutableList<Coin>) {
//            Env.currencyMap.put(currency.name, currency)
//        }
//    } catch(e: Throwable) {
//        logger.error{ e.stackTraceString }
//        System.exit(-1)
//    }


    val userPushServer = PushServer<User>(gson)
    //val dashboardPushServer = PushServer<Boolean>(gson)

    val monitor = ApplicationEvents()

    val started: (Application) -> Unit = {

        logger.debug(Env.message("app.main.start"), it)
    }

    var stopped: (Application) -> Unit = {}
    stopped = {
        monitor.unsubscribe(ApplicationStarted, started)
        monitor.unsubscribe(ApplicationStopped, stopped)
        logger.debug(Env.message("app.main.stop"), it)
    }

    monitor.subscribe(ApplicationStarted, started)
    monitor.subscribe(ApplicationStopped, stopped)


    //val config = ConfigLoader().loadConfigOrThrow<Config>("/application-staging.yaml")

    /* MGK_IMSI
    install(Sessions) {

        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(CachingHeaders) {
        options { outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60), expires = null as? GMTDate?)
                else -> null
            }
        }
    }

    install(DataConversion)


    install(Webjars) {
        path = "/webjars" //defaults to /webjars
        zone = ZoneId.systemDefault() //defaults to ZoneId.systemDefault()
    }
    */

    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val topic = environment.config.config("ktor.kafka.consumer").property("topic").getString()
    val kafkaEventProducer = buildProducer<String, Any>(environment)
    val kafkaEventService = KafkaEventService(topic, kafkaEventProducer)

    val cosumerJobs: ArrayList<BackgroundJob> = ArrayList<BackgroundJob>()

    // Appicable="false" 이면 Consumer 를 띄우지 않는다.
    if (applicable.equals("true")) {
        for (channel in Channel.values()) {
            val topic: String = channel.value
            val conf = BackgroundJob.JobConfiguration()
            conf.name = "Kafka-User-Consumer-" + topic + "-Job"
            @Suppress("UNCHECKED_CAST")
            conf.job = buildConsumer<String, Any>(environment, topic, userPushServer as PushServer<Any>)
            val consumerJob = BackgroundJob(conf)
            conf.job?.let { thread(name = conf.name) { it.run() } }
            cosumerJobs.add(consumerJob)
        }
    }


    //ToDo : 백그라운드 Job 이 한개일 경우 사용
//    install(BackgroundJob.BackgroundJobFeature) {
//        name = "Kafka-User-Consumer-Transaction-Job"
//        job = buildConsumer<String, String>(environment, Channel.TRANSACTION.value, userPushServer as PushServer<Any>)
//    }


    install(ShutDownUrl.ApplicationCallFeature) {
        // The URL that will be intercepted (you can also use the application.conf's ktor.deployment.shutdown.url key)
        shutDownUrl = "/ktor/application/shutdown"
        // A function that will be executed to get the exit code of the process
        exitCodeSupplier = { 0 } // ApplicationCall.() -> Int
    }

    // https://ktor.io/servers/features/forward-headers.html
    install(XForwardedHeaderSupport)

    install(io.ktor.websocket.WebSockets) {
        //ToDo 상용 배포 시 ping 값 주어야 함
        pingPeriod = Duration.ofSeconds(30)
        timeout = Duration.ofSeconds(30)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    logger.debug(Env.message("app.websocket.install"))

    install(Authentication) {
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/v1/user/push") }
    }


    routing {

        static("") {
            resources("assets")
            resources("svg")
            resources("/")
            default("index.html")
        }

        get("/") {
            //call.respondText(Env.greeting, contentType = ContentType.Text.Plain)
            call.respondRedirect("/index.html")
        }

        get("/login") {
            //call.respondText(Env.greeting, contentType = ContentType.Text.Plain)
            call.respondRedirect("/index.html?page=login")
        }

        get("/home") {
            //call.respondText(Env.greeting, contentType = ContentType.Text.Plain)
            call.respondRedirect("/index.html?page=home")
        }

        get("/stat") {
            //call.respondText(Env.greeting, contentType = ContentType.Text.Plain)
            call.respondRedirect("/index.html?page=stat")
        }

        get("/license") {
            //call.respondText(Env.greeting, contentType = ContentType.Text.Plain)
            call.respondRedirect("/index.html?page=license")
        }

        get("/session/counts") {

            val json = gson.toJson(userPushServer.connectionCounts())
            call.respondText(json, contentType = ContentType.Application.Json)
        }

        get("/logging/reset") {

//            synchronized(Env.topicLogging) {
//                Env.topicConsumeCounts.clear()
//                Channel.values().forEach { v ->
//                    run {
//                        try {
//                            File("./log/" + v.value + ".log").delete()
//                        } catch (e: Throwable) {
//
//                        }
//                    }
//                }
//            }
            call.respondRedirect("/index.html?page=stat")
        }

        get("/hello") {
            call.respondText(Env.greeting, contentType = ContentType.Text.Plain)
        }

        post("/kafka") {
            val event = call.receive<KafkaEvent>()
            kafkaEventService.send(event)
            call.respond(HttpStatusCode.Accepted)
        }

        post("/login") {
            val user = call.receive<User>()
            when (user.email) {
                "kepha@korbit.co.kr",
                "danile.ji@korbit.co.kr",
                "franklin@korbit.co.kr",
                "aon@korbit.co.kr",
                "gus@korbit.co.kr" -> {
                    if ((user.email + "-" + user.password) == (user.email + "-" + Env.password)) {
                        val json = "{ " +
                                " \"code\" : 200, " +
                                " \"message\" : \"" + (user.email + "-" + Env.password).sha256() + "\" " +
                                " } "
                        call.respondText(json, contentType = ContentType.Application.Json)
                    } else {
                        val json = "{ " +
                                " \"code\" : 502, " +
                                " \"message\" : \"password mismatch !!!\" " +
                                " } "
                        call.respondText(json, contentType = ContentType.Application.Json)
                    }
                }
                else -> {
                    val json = "{ " +
                            " \"code\" : 501, " +
                            " \"message\" : \"The email is not permitted !!!\" " +
                            " } "
                    call.respondText(json, contentType = ContentType.Application.Json)
                }
            }
        }

        post("/accessToken") {
            val user = call.receive<User>()
            when (user.email) {
                "kepha@korbit.co.kr",
                "danile.ji@korbit.co.kr",
                "franklin@korbit.co.kr",
                "aon@korbit.co.kr",
                "gus@korbit.co.kr" -> {
                    if (user.password == (user.email + "-" + Env.password).sha256()) {
                        val json = "{ " +
                                " \"code\" : 200, " +
                                " \"message\" : \"success\" " +
                                " } "
                        call.respondText(json, contentType = ContentType.Application.Json)
                    } else {
                        val json = "{ " +
                                " \"code\" : 502, " +
                                " \"message\" : \"password mismatch !!!\" " +
                                " } "
                        call.respondText(json, contentType = ContentType.Application.Json)
                    }
                }
                else -> {
                    val json = "{ " +
                            " \"code\" : 501, " +
                            " \"message\" : \"The email is not permitted !!!\" " +
                            " } "
                    call.respondText(json, contentType = ContentType.Application.Json)
                }
            }
        }


        webSocket("/v1/echo") {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    var text = frame.readText()
                    outgoing.send(Frame.Text(text))
                }
            }
        }

        // Websocket Block Start [
        webSocket(path = "/v1/user/push") {

            try {
                //onConnect
                userPushServer.onConnect(this)
                var text:String? = null

                for (frame in incoming) {

                    try {
                        text = null
                        when (frame.frameType) {
                            FrameType.PING -> {
                                this.outgoing.send(Frame.Pong(frame.data))
                            }
                            FrameType.PONG -> {
                                //do nothing
                            }
                            FrameType.CLOSE -> {
                                userPushServer.close(this, CloseReason.Codes.NORMAL, "client socket is close...")
                            }
                            FrameType.BINARY -> {
                                userPushServer.close(
                                    this,
                                    CloseReason.Codes.CANNOT_ACCEPT,
                                    "Unknown binary data recieved so abnomal Close.."
                                )
                            }
                            FrameType.TEXT -> {
                                text = (frame as Frame.Text).readText()

                                if (!Env.isValidWebsocketMessage(text)) {
                                    throw InvalidMessageException("Invalid characters in websocket recv data(json string)")
                                }
                                val message: Message = gson.fromJson(text, Message::class.java)

                                when (message.event) {
                                    Event.SUBSCRIBE.value -> userPushServer.userJoin(
                                        this,
                                        auth.authenticateAccessToken(message.accessToken),
                                        message
                                    )
                                    Event.PUSH_COININFO_MAP.value -> userPushServer.sendTo(
                                        this,
                                        Message(
                                            null,
                                            Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))).time,
                                            Event.PUSH_COININFO_MAP.value,
                                            Env.krwMap as Any
                                        )
                                    )
                                    Event.PUSH_COININFO_LIST.value -> userPushServer.sendTo(
                                        this,
                                        Message(
                                            null,
                                            Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))).time,
                                            Event.PUSH_COININFO_LIST.value,
                                            userPushServer.pushCoinList() as Any
                                        )
                                    )
                                    Event.UNSUBSCRIBE.value -> userPushServer.userLeft(this, message)
                                    Event.HEARTBEAT.value -> userPushServer.heartBeat(this, message)
                                    else -> {
                                        throw InvalidMessageException("Unknown request event type error...")
                                    }
                                }

                            }
                            else -> throw InvalidMessageException("Unknown websocket Message Frame Type Error")
                        }
                    }  catch(e: Exception) {
                        val ip: String = this.call.clientIp

                        var cause: String = e.localizedMessage
                        var code: CloseReason.Codes = CloseReason.Codes.PROTOCOL_ERROR
                        when (e) {
                            is ClosedReceiveChannelException -> {
                                code = CloseReason.Codes.GOING_AWAY
                            }
                            is InvalidMessageException -> {
                            }
                            is JsonSyntaxException -> {
                                cause = "Websocket JsonSyntaxError when receiving message ..."
                            }
                            is IllegalStateException -> {
                                cause = "Websocket JsonSyntaxError when receiving message ..."
                            }
                        }
                        if (e is ClosedReceiveChannelException) {
                            logger.debug("[$ip]-" + cause + ":" + (text ?: "") + " : " + e.stackTraceString)
                        } else {
                            logger.error("[$ip]-" + cause + ":" + (text ?: ""))
                            userPushServer.sendError(this, text, cause  )
                        }
                    }
                }

                val ip = this.call.clientIp
                logger.debug("[$ip]-" + Env.message("app.websocket.close"), "client's socket is broken or closed");
                userPushServer.close(this, CloseReason.Codes.GOING_AWAY, "client's socket is broken or closed")

            } catch(e: Exception ) {
                logger.error(e.stackTraceString)
            }
        }
        // Websocket Block End ]

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }
    }

    //1주일 지난 데이타 지우는 타이머(하루에 1번씩) [
    if(Env.topicLogging) {
        Timer("OldTopicLogRemoveTimer", true).scheduleAtFixedRate(timerTask {

            try {

                val df = DateTimeFormatter.ofPattern("-yyyyMMdd")
                val current = LocalDateTime.now(ZoneId.of("UTC"))
                val aWeekBefore = current.minusDays(Env.topicLogRollingPeriod)
                val aWeekBeforeStr = df.format(aWeekBefore)


                File("./log").listFiles(FilenameFilter { dir, name ->
                    name.replace(
                        "(((ticker)|(orderbook)|(transaction))-[0-9]{8}\\.log)|(((ticker)|(orderbook)|(transaction))\\.tlog)".toRegex(),
                        ""
                    ).isEmpty()
                }).forEach {
                    run {
                        try {
                            val dateStr =
                                it.nameWithoutExtension.replace("((ticker)|(orderbook)|(transaction))".toRegex(), "")
                            when(dateStr) {
                                "" -> {
                                    Env.topicConsumeCounts.remove(it.nameWithoutExtension)
                                    it.delete()
                                }
                                else -> {

                                    if (dateStr.compareTo(aWeekBeforeStr) < 0) {
                                        Env.topicConsumeCounts.remove(it.nameWithoutExtension)
                                        it.delete()
                                    } else {

                                    }
                                }
                            }

                        } catch (e: Throwable) {
                            logger.error { e.stackTraceString }
                        }
                    }
                }

            } catch (e: Throwable) {
                logger.error(e) { Env.message("app.main.timer.scheduleError") + e.stackTrace }
            }
        }, 300, 864000000)
    }
    //1주일 지난 데이타 지움 ]

    /*ToDo : 아래는 테스트를 위한 타이머 코드 추후 삭제 필요
    Timer("PushKrwMarketMap", false).scheduleAtFixedRate(timerTask{

        try {
            //if( userPushServer.sessionMap.size > 0 ) {

                val list = ArrayList<Coin>()
                for (x in 0..1) {
                    val idx = kotlin.math.abs(random.nextInt()) % Env.krwList.size
                    val coin = Env.krwMap[Env.krwList[idx]] as Coin
                    if (coin.variance == 0.00f) {
                        coin.variance = 1.00f
                        coin.price = coin.price + (coin.price * coin.variance) / 100.0f
                    } else {
                        coin.variance = -coin.variance
                        coin.price = coin.price + (coin.price * coin.variance) / 100.0f
                    }
                    list.add(coin)
                }
                for(x in 1..100) {
                    val event = KafkaEvent(KafkaEventType.COIN_LIST.type, Instant.now(), list as Any)
                    kafkaEventService.send(event)
                }
            //}
            logger.info(Env.message("app.pushServer.broadcastStart"), userPushServer.sessionMap.size)

        }catch(e: Throwable) {
            logger.error(e) { Env.message("app.main.timer.scheduleError") +  e.stackTrace }
        }
    }, 1000L, 1000L)

    */
}

fun main() {
    print(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))))
}