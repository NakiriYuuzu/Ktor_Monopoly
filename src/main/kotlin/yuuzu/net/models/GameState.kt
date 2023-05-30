package yuuzu.net.models

enum class GameState {
    Error,
    Notice,
    Data
}

class ErrorException(message: String) : Exception(message)
class NoticeException(message: String) : Exception(message)