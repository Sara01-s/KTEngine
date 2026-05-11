package engine.utils

import engine.EngineSettings

fun logInfo(msg: String) {
    if (EngineSettings.ALLOW_LOGS) {
        println("[INFO] $msg")
    }
}

fun logWarning(msg: String) {
    if (EngineSettings.ALLOW_LOGS) {
        println("[Warning] $msg")
    }
}

fun logError(msg: String) {
    if (EngineSettings.ALLOW_LOGS) {
        println("[Error] $msg")
    }
}



