package kr.co.korbit.fisherman.pushserver.model

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.HashMap

enum class Event(val value: String) {
    CONNECT("korbit:connect"  ),
    CONNECTED("korbit:connected"),
    DISCONNECTED("korbit:disconnected"),
    CLOSED ("korbit:disconnected"),
    SUBSCRIBE("korbit:subscribe") ,
    UNSUBSCRIBE("korbit:unsubscribe"),
    HEARTBEAT("korbit:heartbeat"),
    PUSH_TICKER("korbit:push-ticker"),
    PUSH_ORDERBOOK("korbit:push-orderbook"),
    PUSH_TRANSACTION("korbit:push-transaction"),
    PUSH_COININFO("korbit:push-coininfo"),
    PUSH_COININFO_LIST("korbit:push-coininfo-list"),
    PUSH_COININFO_MAP("korbit:push-coininfo-map"),
    SUCCESS("korbit:success"),
    ERROR("korbit:error")
}

enum class KafkaEventType(val type: Int) {
    COIN(0),
    COIN_LIST(1),
    COIN_MAP(1)
}

data class Message(
    val accessToken: String? ,
    val timestamp: Long,
    val event: String,
    val data: Any
)