package yuuzu.net.models

import io.ktor.websocket.*

data class Player(
    val id: String,
    val leader: Boolean,
    val name: String,
    var session: DefaultWebSocketSession?= null,
    var location: Int,
    var status: PlayerStatus,
    var money: Int
)
