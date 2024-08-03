package kr.co.korbit.fisherman.pushserver.model

import java.math.BigDecimal

data class  Order(
    val price: BigDecimal,
    val amount: BigDecimal
)