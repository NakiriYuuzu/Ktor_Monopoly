package yuuzu.net.data

import io.ktor.websocket.*
import yuuzu.net.models.*

class RoomEvent(roomId: String) {
    private val players = mutableMapOf<String, Player>()
    private val properties = mutableListOf<GameProperties>()

    init {
        properties.add(GameProperties(1, "基隆", "廟口夜市", 200, 0))
        properties.add(GameProperties(2, "台北", "101大樓", 200, 0))
        properties.add(GameProperties(3, "新北", "平溪（天燈）", 200, 0))
        properties.add(GameProperties(4, "桃園", "桃園機場", 200, 0))
        properties.add(GameProperties(5, "新竹", "風力發電機", 200, 0))
        properties.add(GameProperties(6, "苗栗", "龍騰斷橋", 200, 0))
        properties.add(GameProperties(7, "靜宜大學", "主顧聖母堂", 200, 0))
        properties.add(GameProperties(8, "台中", "台中歌劇院", 200, 0))
        properties.add(GameProperties(9, "彰化", "八掛山（佛像）", 200, 0))
        properties.add(GameProperties(10, "南投", "日月潭（九蛙）", 200, 0))
        properties.add(GameProperties(11, "雲林", "劍湖山", 200, 0))
        properties.add(GameProperties(12, "嘉義", "阿里山小火車", 200, 0))
        properties.add(GameProperties(13, "台南", "平安古堡", 200, 0))
        properties.add(GameProperties(14, "高雄", "85大樓", 200, 0))
        properties.add(GameProperties(15, "屏東", "海生館（鯨魚親水廣場）", 200, 0))
        properties.add(GameProperties(16, "台東", "鹿野高台（熱氣球）", 200, 0))
        properties.add(GameProperties(17, "花蓮", "七星潭", 200, 0))
        properties.add(GameProperties(18, "宜蘭", "清水地熱", 200, 0))
    }

    fun join(player: Player): Boolean {
        if (players.size >= Constant.MAX_PLAYER) {
            return false
        }

        players[player.id] = player
        logb("RoomEvent->join: ${players.values}")
        return true
    }

    fun leave(playerId: String): Boolean {
        if (!players.containsKey(playerId)) {
            return false
        }

        players.remove(playerId)
        return true
    }

    suspend fun broadcastToPlayer(player: Player, message: String) {
        player.session?.send(message)
    }

    suspend fun broadcastToPlayers(message: String) {
        players.values.forEach { player ->
            player.session?.send("${player.name}: $message")
        }
    }

    suspend fun broadcastToPlayers(sender: String, message: String) {
        players.values.forEach { player ->
            player.session?.send("$sender: $message")
        }
    }

    fun getPlayers(): List<Player> {
        return players.values.toList()
    }

    fun getPlayerByName(playerName: String): Player {
        return players.values.first { it.name == playerName }
    }

    fun checkEveryoneStatus(status: PlayerStatus): Boolean {
        return players.values.all { it.status == status }
    }

    /** 處理游戲事件轉發 **/

    fun setToReady() {
        players.values.forEach { player ->
            player.status = PlayerStatus.Ready
        }
    }

    fun setPlayerStatus(player: Player, status: PlayerStatus) {
        players[player.id]?.status = status
    }

    fun setPlayerPosition(player: Player, dice: Int): Boolean {
        var currentLocation = players[player.id]?.location?.plus(dice) ?: 0

        if (currentLocation >= properties.size) {
            currentLocation -= properties.size
        }

        players[player.id]?.copy(
            location = currentLocation,
            status = PlayerStatus.DiceRolled,
            money = players[player.id]?.money?.plus(properties[currentLocation].propertyRent) ?: throw ErrorException("${player.name} money is null")
        )?.also { players[player.id] = it }

        if (players[player.id]?.money!! >= 10000 || players[player.id]?.money!! <= 0) {
            // TODO: 結束遊戲
            logb("RoomEvent->setPlayerPosition: ${players[player.id]?.name} Winner Or Loser.")
        }

        logb("RoomEvent->setPlayerPosition: ${players[player.id]}")

        return currentLocation % 2 == 0
    }
}