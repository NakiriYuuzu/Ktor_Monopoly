package yuuzu.net.plugins

import io.ktor.server.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.routing.*
import yuuzu.net.data.RoomManager
import yuuzu.net.routes.gameWebSocketRoute

fun Application.configureSockets(roomManager: RoomManager) {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        gameWebSocketRoute(roomManager)
    }
}
