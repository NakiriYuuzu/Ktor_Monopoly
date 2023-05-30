package yuuzu.net.routes

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import yuuzu.net.data.GameEvent
import yuuzu.net.data.RoomManager
import yuuzu.net.data.handleGameEvent
import yuuzu.net.models.GameState
import yuuzu.net.models.NoticeException
import yuuzu.net.models.loge
import yuuzu.net.models.logy

fun Route.gameWebSocketRoute(roomManager: RoomManager) {

    webSocket("/play/{roomId}/{playerName}") {
        val roomId = call.parameters["roomId"] ?: return@webSocket
        val playerName = call.parameters["playerName"] ?: return@webSocket

        logy("WebSocket->roomId: RoomId: $roomId | PlayerName: $playerName")

        val roomEvent = roomManager.getRoom(roomId)
        if (roomEvent == null) {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Room not found."))
            return@webSocket
        }

        val player = roomManager.getPlayer(roomId, playerName)
        if (player == null) {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Player not found."))
            return@webSocket
        }

        player.session = this
        send("Player $playerName joined room $roomId.")

        for (frame in incoming) {
            if (frame is Frame.Text) {
                val receivedText = frame.readText()
                logy("WebSocket->frame: $receivedText")

                try {
                    val event = GameEvent.valueOf(receivedText)
                    logy("WebSocket->event: $event")
                    handleGameEvent(player, event, roomEvent)

                } catch (e: Exception) {
                    loge("WebSocket->Error: ${e.localizedMessage}")
                    when (e) {
                        is NoticeException -> {
                            roomEvent.broadcastToPlayers("${GameState.Notice}", e.localizedMessage)
                        }

                        else -> {
                            roomEvent.broadcastToPlayers("${GameState.Error}", e.localizedMessage)
                        }
                    }
                }
            }
        }
    }
}