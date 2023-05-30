package yuuzu.net.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import yuuzu.net.data.RoomManager
import yuuzu.net.routes.roomRoute

fun Application.configureRouting(roomManager: RoomManager) {
    routing {
        roomRoute(roomManager)
    }
}
