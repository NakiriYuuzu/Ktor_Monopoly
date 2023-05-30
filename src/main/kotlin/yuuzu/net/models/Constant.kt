package yuuzu.net.models

object Constant {
    const val MAX_PLAYER = 4
}

fun loge(message: String) {
    println("\u001B[31m$message\u001B[0m")
}

fun logg(message: String) {
    println("\u001B[32m$message\u001B[0m")
}

fun logy(message: String) {
    println("\u001B[33m$message\u001B[0m")
}

fun logb(message: String) {
    println("\u001B[34m$message\u001B[0m")
}