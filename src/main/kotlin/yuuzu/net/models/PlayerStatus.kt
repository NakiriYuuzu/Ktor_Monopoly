package yuuzu.net.models

enum class PlayerStatus {
    Waiting, // 等待中
    Ready, // 玩家準備好了
    DiceRolled, // 玩家擲骰子了
    InEvent, // 玩家在事件中
    FinishTurn, // 玩家結束回合
    Bankrupt, // 破產
    Winner // 獲勝
}