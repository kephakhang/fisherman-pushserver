@file:Suppress("NAME_SHADOWING")
package kr.co.korbit.fisherman.pushserver.env

import com.sksamuel.hoplite.ConfigLoader
import com.typesafe.config.ConfigFactory
import io.ktor.application.ApplicationEnvironment
import io.ktor.config.ApplicationConfigValue
import io.ktor.config.HoconApplicationConfig
import kr.co.korbit.fisherman.pushserver.model.*
import java.util.Locale
import mu.KotlinLogging
import java.security.MessageDigest
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap

private val logger = KotlinLogging.logger {}

class Env {
    companion object {

        val password = "vmffotvhaxla"
        val korbit = "korbit"
        val connectionsLimit = 10
        val lang = Locale.getDefault().language
        val topicConsumeCounts: ConcurrentHashMap<String, Int> = ConcurrentHashMap<String, Int>()
        var topicLogging: Boolean = false
        val topicLogRollingPeriod: Long = 7L
        var branch = "local"

        @UseExperimental(io.ktor.util.KtorExperimentalAPI::class)
        val messageConfig = HoconApplicationConfig(ConfigFactory.load("i18n/" + lang))
        val channelNum: Int = 2
        val greeting: String = "HELLO This is Kobit's push server!"
        val normalClosureMessage: String = "Normal closure"
//        var currencyMap  = ConcurrentHashMap<String, Coin>()
//        var currencyList: MutableList<Coin>? = null
        val krwList: List<String> = listOf(
            "btc-krw" ,
            "eth-krw" ,
            "xrp-krw" ,
            "bch-krw" ,
            "eos-krw" ,
            "etc-krw" ,
            "bsv-krw" ,
            "zil-krw" ,
            "ltc-krw" ,
            "trx-krw" ,
            "fet-krw" ,
            "qtum-krw" ,
            "usdc-krw" ,
            "aergo-krw" ,
            "xlm-krw" ,
            "bnb-krw" ,
            "omg-krw" ,
            "link-krw" ,
            "med-krw" ,
            "bat-krw" ,
            "loom-krw" ,
            "dai-krw" ,
            "knc-krw" ,
            "mkr-krw" ,
            "poly-krw"
        )
        val krwMap: Map<String, Any> = mapOf(
            "btc-krw"   to  Coin("btc-krw", "BTC 비트코인",	10500500f	,1.06f),
            "eth-krw"   to  Coin( "eth-krw", "ETH 이더리움",	272050f	,2.18f ),
            "xrp-krw"   to  Coin( "xrp-krw", "XRP 리플"	, 281.3f	,1.26f ),
            "bch-krw"   to  Coin( "bch-krw", "BCH 비트코인 캐시",	394550f	,3.39f ),
            "eos-krw"   to  Coin( "eos-krw", "EOS 이오스",	4380f	,1.98f ),
            "etc-krw"   to  Coin( "etc-krw", "ETC 이더리움 클래식",	9820f	,1.76f ),
            "bsv-krw"   to  Coin( "bsv-krw", "BSV 비트코인SV",	296000f	,6.02f ),
            "zil-krw"   to  Coin( "zil-krw", "ZIL 질리카",	7.28f	,1.11f ),
            "ltc-krw"   to  Coin( "ltc-krw", "LTC 라이트 코인",	72400f	,3.21f ),
            "trx-krw"   to  Coin( "trx-krw", "TRX 트론"	, 20.51f	,1.38f ),
            "fet-krw"   to  Coin( "fet-krw", "FET 페치"	, 41.0f,	0.00f),
            "qtum-krw"  to  Coin( "qtum-krw", "QTUM 퀀텀",	2525f	,0.40f ),
            "usdc-krw"  to  Coin( "usdc-krw", "USDC 유에스디코인", 	1198f	,-0.58f ),
            "aergo-krw" to  Coin( "aergo-krw", "AERGO 아르고",	35.6f	,-2.73f ),
            "xlm-krw"   to  Coin( "xlm-krw", "XLM 스텔라루멘",	70.5f	,2.92f ),
            "bnb-krw"   to  Coin( "bnb-krw", "BNB 바이낸스 코인",	23120f	,0.04f ),
            "omg-krw"   to  Coin( "omg-krw", "OMG 오미세고",	1005f	,1.01f ),
            "link-krw"  to  Coin( "link-krw", "LINK 체인링크",	4700f	,-24.19f ),
            "med-krw"   to  Coin( "med-krw", "MED 메디블록",	4.22f	,9.33f ),
            "bat-krw"   to  Coin( "bat-krw", "BAT 베이직어텐션토큰",	255.4f	,2.16f ),
            "loom-krw"  to  Coin( "loom-krw", "LOOM 룸네트워크",	25.0f	,-1.19f ),
            "dai-krw"   to  Coin( "dai-krw", "DAI 다이"	, 1160f	,0.00f ),
            "knc-krw"   to  Coin( "knc-krw", "KNC 카이버네트워크",	758.2f	,-29.47f ),
            "mkr-krw"   to  Coin( "mkr-krw", "MKR 메이커",	600000f	,0.00f ),
            "poly-krw"  to  Coin( "poly-krw", "POLY 폴리매쓰", 29.3f	,0.00f )
        )

        fun String.md5(): String {
            return hashString(this, "MD5")
        }

        fun String.sha256(): String {
            return hashString(this, "SHA-256")
        }

        fun hashString(input: String, algorithm: String): String {
            return MessageDigest
                .getInstance(algorithm)
                .digest(input.toByteArray())
                .fold("", { str, it -> str + "%02x".format(it) })
        }

        fun isValidWebsocketMessage(input: String): Boolean {
            return input.replace("[0-9a-zA-Z_\\s\"{},\\.\\:\\[\\]]+".toRegex(),"").isEmpty()
        }

        @UseExperimental(io.ktor.util.KtorExperimentalAPI::class)
        fun messageProp(key: String): ApplicationConfigValue? {
            try {
                val idx: Int = key.lastIndexOf(".")
                val group: String = key.substring(0, idx)
                val key: String = key.substring(idx + 1)
                @UseExperimental(io.ktor.util.KtorExperimentalAPI::class)
                return Env.messageConfig.config(group).property(key)
            }catch(e: Throwable) {
                logger.error( "Env.message : " +  e.stackTrace)
                @UseExperimental(io.ktor.util.KtorExperimentalAPI::class)
                return null
            }
        }

        fun message(key: String): String {
            try {
                val idx: Int = key.lastIndexOf(".")
                val group: String = key.substring(0, idx)
                val key: String = key.substring(idx + 1)
                logger.debug("Evn.message - group : " + group)
                logger.debug("Evn.message - key : " + key)
                @UseExperimental(io.ktor.util.KtorExperimentalAPI::class)
                return Env.messageConfig.config(group).property(key).getString()
            }catch(e: Throwable) {
                logger.error( "Env.message : " +  e.stackTrace)
                return ""
            }
        }

        fun error(): Message {
            return Message(
                null,
                Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))).time,
                Event.ERROR.value,
                ChannelList()
            );
        }
    }
}
