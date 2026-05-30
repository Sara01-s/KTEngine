package engine.utils

fun log(message: Any?) {
    println("[INFO] $message")
}

fun logWarn(message: Any?) {
    System.err.println("[WARNING] $message")
}

fun logError(message: Any?) {
    System.err.println("[ERROR] $message")
}

