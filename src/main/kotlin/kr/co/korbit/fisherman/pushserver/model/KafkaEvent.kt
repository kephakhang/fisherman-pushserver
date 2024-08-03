package kr.co.korbit.fisherman.pushserver.model

import java.time.Instant

data class KafkaEvent(val type: Int , val timestamp: Instant, val data: Any)
