package engine.utils

import engine.EngineSettings

enum class LogLevel {
    Info,
    Warning,
    Error,
}

fun log(message: Any?, level: LogLevel = LogLevel.Info) {
    if (!EngineSettings.ALLOW_LOGS) {
        return
    }

    when (level) {
        LogLevel.Info -> { println("[INFO] $message") }
        LogLevel.Warning -> { println("[WARNING] $message") }
        LogLevel.Error -> { println("[ERROR] $message") }
    }
}



