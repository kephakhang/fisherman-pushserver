package kr.co.korbit.fisherman.pushserver.model

import java.math.BigDecimal

data class  Transaction(
    val channel: String,
    val currency_pair: String,
    val timestamp: Long,
    val price: BigDecimal,
    val amount: BigDecimal,
    val taker: String
)