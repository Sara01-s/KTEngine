package engine.utils

import engine.EngineSettings
import org.lwjgl.opengl.GL11.*

object GLDebug {

    var errorCount: Int = 0
        private set

    var crashOnGLError: Boolean = false

    private fun glErrorToString(error: Int): String {
        return when (error) {
            GL_NO_ERROR -> "GL_NO_ERROR"
            GL_INVALID_ENUM -> "GL_INVALID_ENUM"
            GL_INVALID_VALUE -> "GL_INVALID_VALUE"
            GL_INVALID_OPERATION -> "GL_INVALID_OPERATION"
            GL_STACK_OVERFLOW -> "GL_STACK_OVERFLOW"
            GL_STACK_UNDERFLOW -> "GL_STACK_UNDERFLOW"
            GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY"
            else -> "UNKNOWN_ERROR($error)"
        }
    }

    infix fun glCall(block: () -> Unit) {
        block()

        if (!EngineSettings.ALLOW_LOGS) {
            return
        }

        var error = glGetError()

        while (error != GL_NO_ERROR) {
            errorCount++

            val stack = Thread.currentThread().stackTrace
            val caller = stack.getOrNull(3)

            val location = "${caller?.fileName}:${caller?.lineNumber}"
            val message = glErrorToString(error)

            val fullMessage = "[OpenGL ERROR #$errorCount] $message at ${caller?.methodName} ($location)"

            println(fullMessage)

            if (crashOnGLError) {
                error("OpenGL crash enabled → $fullMessage")
            }

            error = glGetError()
        }
    }
}