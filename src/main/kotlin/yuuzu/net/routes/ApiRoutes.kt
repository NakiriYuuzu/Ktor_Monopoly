package yuuzu.net.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import yuuzu.net.data.RoomManager
import yuuzu.net.models.JoinRequest
import yuuzu.net.models.CreateRequest
import yuuzu.net.models.logy

fun Route.roomRoute(roomManager: RoomManager) {
    route("/api") {
        post("/createRoom") {
            val data = call.receive<CreateRequest>()
            logy("api->CreateRoom: $data")

            if (data.playerName.isEmpty()) {
                call.respondText("playerName is Empty.", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@post
            }

            call.respond(mapOf("roomId" to roomManager.createRoom(data.playerName)))
        }

        post("/joinRoom") {
            val data = call.receive<JoinRequest>()
            logy("api->JoinRoom: $data")

            if (data.roomId.isEmpty() || data.playerName.isEmpty()) {
                call.respondText("roomId | playerName is Empty.", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@post

            }

            val result = roomManager.joinRoom(data.roomId, data.playerName)
            if (result != "Success") {
                call.respondText(result, status = io.ktor.http.HttpStatusCode.BadRequest)
                return@post
            }

            call.respond(mapOf("result" to result))
        }
    }
}