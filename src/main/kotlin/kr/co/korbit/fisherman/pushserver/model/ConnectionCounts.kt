package kr.co.korbit.fisherman.pushserver.model


data class ConnectionCounts(
    val totalCount: Int,
    val subscriptions: List<SubscriptionCount>
)