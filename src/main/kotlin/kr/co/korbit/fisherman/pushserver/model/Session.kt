package kr.co.korbit.fisherman.pushserver.model

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.websocket.WebSocketServerSession

data class Session<Any>(val socketSession: WebSocketServerSession, val session: Any)
