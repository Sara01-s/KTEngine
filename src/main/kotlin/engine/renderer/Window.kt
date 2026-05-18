package engine.renderer

import engine.utils.GLDebug.glCall
import engine.utils.LogLevel
import engine.utils.log
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.glViewport

class Window(
    width: Int = 1280,
    height: Int = 720,
    title: String = "Default Window",
) : AutoCloseable{
    companion object {
        var width = 0
        var height = 0
        val aspectRatio get() = width.toFloat() / height.toFloat()
    }

    val handle: Long

    init {
        if (!glfwInit()) {
            log("Failed to initialize GLFW", LogLevel.Error)
        }

        Window.width = width
        Window.height = height

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

        handle = glfwCreateWindow(
            width,
            height,
            title,
            0,
            0
        )

        if (handle == 0L) {
            log("Failed to create GLFW window", LogLevel.Error)
        }

        glfwMakeContextCurrent(handle)
        GL.createCapabilities()
        glCall { glViewport(0, 0, width, height) }
        glfwSetFramebufferSizeCallback(handle) { _, w, h ->
            Window.width = w
            Window.height = h
            glCall { glViewport(0, 0, width, height) }
        }
        setVSync(enabled = true)
        glfwShowWindow(handle)
    }

    fun isOpen(): Boolean {
        return !glfwWindowShouldClose(handle)
    }

    fun setVSync(enabled: Boolean) {
        glfwSwapInterval(if (enabled) 1 else 0)
    }

    fun swapBuffers() {
        glfwSwapBuffers(handle)
    }

    fun pollEvents() {
        glfwPollEvents()
    }

    override fun close() {
        glfwDestroyWindow(handle)
        glfwTerminate()
    }
}