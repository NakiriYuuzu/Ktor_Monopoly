package yuuzu.net.data

import yuuzu.net.models.*

enum class GameEvent {
    StartGame,
    EndGame,
    DiceRoll,
    QuestionTime,
    MiniGame,
}

suspend fun handleGameEvent(player: Player, event: GameEvent, roomEvent: RoomEvent) {
    when (event) {
        GameEvent.StartGame -> {
            logg("GameEvent>StartGame: $roomEvent")
            logg("GameEvent>StartGame: $player")
            if (player.leader) {
                roomEvent.setToReady()
                roomEvent.broadcastToPlayers("${GameState.Notice}", "遊戲開始！")
            } else {
                throw NoticeException("你不是隊長！")
            }
        }

        GameEvent.EndGame -> {
            logg("GameEvent>EndGame: $roomEvent")
            // TODO: End game
        }

        GameEvent.DiceRoll -> {
            logg("${GameEvent.DiceRoll}: $player")

            if (player.status != PlayerStatus.Ready) {
                when (player.status) {
                    PlayerStatus.Waiting -> {
                        throw NoticeException("${player.name} 正在等待中。。。")
                    }

                    PlayerStatus.DiceRolled -> {
                        throw NoticeException("${player.name} 已經擲骰子了！")
                    }

                    PlayerStatus.InEvent -> {
                        throw NoticeException("${player.name} 正在事件中！")
                    }

                    PlayerStatus.FinishTurn -> {
                        if (roomEvent.checkEveryoneStatus(PlayerStatus.FinishTurn)) {
                            roomEvent.setToReady()
                        }
                    }

                    PlayerStatus.Bankrupt -> {
                        throw NoticeException("${player.name} 已經破產了！")
                    }

                    PlayerStatus.Winner -> {
                        throw NoticeException("${player.name} 已經獲勝了！")
                    }
                    else -> {
                        throw ErrorException("${player.name} has an error ${player.status}!")
                    }
                }
            }


            val diceRoll = (1..6).random()
            roomEvent.broadcastToPlayers(player.name, "rolled $diceRoll")

            if (roomEvent.setPlayerPosition(player, diceRoll)) {
                roomEvent.broadcastToPlayer(player, "${GameEvent.QuestionTime}")
            } else {
                roomEvent.broadcastToPlayer(player, "${GameEvent.MiniGame}")
            }
        }

        GameEvent.QuestionTime -> {
            logg("${GameEvent.QuestionTime}: $player")
            roomEvent.setToReady()
        }

        GameEvent.MiniGame -> {
            logg("${GameEvent.MiniGame}: $player")
            roomEvent.setToReady()
        }
    }
}