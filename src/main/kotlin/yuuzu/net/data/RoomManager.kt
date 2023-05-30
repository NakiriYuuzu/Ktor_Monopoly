package yuuzu.net.data

import yuuzu.net.models.*
import java.util.UUID
import kotlin.random.Random

class RoomManager {
    private val rooms = mutableMapOf<String, Room>()

    fun createRoom(playerName: String): String {
        var id: String
        do {
            id = generateRandomId()
        } while (rooms.containsKey(id))

        val room = RoomEvent(id)
        val player = Player(
            id = UUID.randomUUID().toString(),
            name = playerName,
            leader = true,
            location = 0,
            status = PlayerStatus.Waiting,
            money = 5000
        )

        if (!room.join(player)) throw ErrorException("Failed to join room")
        rooms[id] = Room(1, room)
        loge("$rooms")

        return id
    }

    fun joinRoom(roomId: String, playerName: String): String {
        if (!rooms.containsKey(roomId)) return "Room not found"
        val room = rooms[roomId] ?: return "Room not found"
        if (room.count >= Constant.MAX_PLAYER) return "Room is full"

        val players = room.room.getPlayers()
        if (players.any { it.name.lowercase() == playerName.lowercase() }) return "Player name is already taken"

        val player = Player(
            id = UUID.randomUUID().toString(),
            name = playerName,
            leader = false,
            location = 0,
            status = PlayerStatus.Waiting,
            money = 5000
        )

        room.count ++
        loge("roomManager->joinRoom: $rooms")
        if (!room.room.join(player)) return "Failed to join room"
        return "Success"
    }

    fun leaveRoom(roomId: String, playerName: String) {
        val room = rooms[roomId] ?: throw ErrorException("Room not found")
        room.count --

        if (room.count == 0) {
            rooms.remove(roomId)
        }

        room.room.leave(playerName)
    }

    fun getRoom(id: String): RoomEvent? {
        loge("getRoom: $rooms")
        return rooms[id]?.room
    }

    fun getPlayer(roomId: String, playerName: String): Player? {
        val room = rooms[roomId] ?: throw ErrorException("Room not found")
        val players =room.room.getPlayers()
        return players.find { it.name.lowercase() == playerName.lowercase() }
    }

    private fun generateRandomId(): String {
        return (1..6).map { Random.nextInt(0, 10) }.joinToString("")
    }
}