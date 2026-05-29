package engine.utils

enum class LogLevel {
    Info,
    Warning,
    Error,
}

fun log(message: Any?, level: LogLevel = LogLevel.Info) {
    when (level) {
        LogLevel.Info -> { println("[INFO] $message") }
        LogLevel.Warning -> System.err.println("[WARNING] $message")
        LogLevel.Error   -> System.err.println("[ERROR] $message")
    }
}



