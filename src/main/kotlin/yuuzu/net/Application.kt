package yuuzu.net

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import yuuzu.net.data.RoomManager
import yuuzu.net.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val roomManager = RoomManager()

    configureSecurity()
    configureSerialization()
    configureSockets(roomManager)
    configureRouting(roomManager)
}
