package yuuzu.net.models

import yuuzu.net.data.RoomEvent

data class Room(
    var count: Int,
    val room: RoomEvent
)