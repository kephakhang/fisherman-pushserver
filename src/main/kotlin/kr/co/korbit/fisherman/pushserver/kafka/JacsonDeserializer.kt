package kr.co.korbit.fisherman.pushserver.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import kr.co.korbit.fisherman.exception.stackTraceString
import kr.co.korbit.fisherman.pushserver.model.*
import mu.KotlinLogging
import org.apache.kafka.common.serialization.Deserializer
import java.nio.charset.StandardCharsets

private val logger = KotlinLogging.logger {}

class JacksonDeserializer(
    private val objectMapper: ObjectMapper = ObjectMapper()
        .registerModule(JavaTimeModule())
        .registerModule(KotlinModule())
) : Deserializer<Any?> {


    override fun deserialize(topic: String, data: ByteArray): Any {
        try {

            when (topic) {
                Channel.TICKER.value -> {
                    val ticker: Ticker = objectMapper.readValue<Ticker>(data)
                    logger.debug { "kafka data : " + String(data, StandardCharsets.UTF_8) }
                    return ticker
                }
                Channel.ORDERBOOK.value -> {
                    val orderbook: Orderbook = objectMapper.readValue<Orderbook>(data)
                    logger.debug { "kafka data : " + String(data, StandardCharsets.UTF_8) }
                    return orderbook
                }
                Channel.TRANSACTION.value -> {
                    val trasactionList: Array<Transaction> = objectMapper.readValue<Array<Transaction>>(data)
                    logger.debug { "kafka data : " + String(data, StandardCharsets.UTF_8) }
                    return trasactionList
                }
                Channel.TEST.value -> {
                    val kafkaEvent: KafkaEvent = objectMapper.readValue<KafkaEvent>(data)
                    logger.debug { "kafka data : " + String(data, StandardCharsets.UTF_8) }
                    return kafkaEvent
                }
                else -> throw Exception("unknown topic data format")
            }

        } catch (e: Throwable) {
            logger.warn{ "deserializing JSON message error : " + e.stackTraceString}
            return e
        }
    }
}