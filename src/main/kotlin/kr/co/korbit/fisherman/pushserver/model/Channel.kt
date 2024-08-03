package kr.co.korbit.fisherman.pushserver.model

import com.google.gson.internal.LinkedTreeMap
import java.util.LinkedHashMap

enum class Channel(val value: String, val no: Int) {
    TEST("test", 0),
    TICKER("ticker", 1),
    ORDERBOOK("orderbook", 2),
    TRANSACTION("transaction", 3)
}

//channel: ["ticker", "orderbook:btc_krw,eth_krw"]
typealias ChannelList = LinkedTreeMap<String, Any>