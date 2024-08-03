package kr.co.korbit.fisherman.pushserver.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kr.co.korbit.fisherman.pushserver.env.Env
import mu.KotlinLogging
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.StringDeserializer
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

private val logger = KotlinLogging.logger {}

class KeyDeserializer(
    private val objectMapper: ObjectMapper = ObjectMapper()
        .registerModule(JavaTimeModule())
        .registerModule(KotlinModule())
) : Deserializer<String?> {

    override fun deserialize(topic: String, data: ByteArray): String {
        return try {
            data?.let {
                val key = String(it, Charset.defaultCharset())
                if( !key.equals(Env.korbit) ) {
                    if (Env.branch.equals("master")) {
                        logger.error("unkown kafka event's key : " + key)
                        return "unknown"
                    } else {
                        return Env.korbit
                    }
                } else {
                    return Env.korbit
                }
            }

        } catch (e: Throwable) {

            if( Env.branch.equals("master") ) {
                logger.error("Error when deserializing of kafka event's key byte[] to string : " + e.stackTrace)
                return "unknown"
            } else {
                logger.warn("Error when deserializing of kafka event's key byte[] to string : " + e.stackTrace)
                return Env.korbit
            }
        }
    }
}