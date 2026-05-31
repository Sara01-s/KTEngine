package engine.rendering

import engine.systems.RenderSystem
import engine.utils.log
import engine.utils.logError
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL43.*
import org.lwjgl.opengl.GLDebugMessageCallback
import org.lwjgl.system.MemoryUtil.NULL

object Window : AutoCloseable {
    var width = 1280
    var height = 720
    var title = "KTEngine uwu"
    val aspectRatio get() = width.toFloat() / height.toFloat()

    val handle: Long

    private var isFullscreen = false
    private var windowedX = 100
    private var windowedY = 100
    private var windowedWidth = 1280
    private var windowedHeight = 720

    init {
        if (!glfwInit()) {
            logError("Failed to initialize GLFW")
        }

        glfwDefaultWindowHints()

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_SAMPLES, 4)

        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE)

        handle = glfwCreateWindow(width, height, title, NULL, NULL)

        if (handle == NULL) {
            logError("Failed to create GLFW window")
        }

        val monitor = glfwGetPrimaryMonitor()
        val videoMode = glfwGetVideoMode(monitor)

        if (videoMode != null) {
            val centerX = (videoMode.width() - width) / 2
            val centerY = (videoMode.height() - height) / 2

            glfwSetWindowPos(handle, centerX, centerY)
        }

        glfwMakeContextCurrent(handle)
        GL.createCapabilities()

        setupGLState()
        setupDebugCallback()

        glfwSetFramebufferSizeCallback(handle) { _, newWidth, newHeight ->
            if (newWidth > 0 && newHeight > 0) {
                glViewport(0, 0, newWidth, newHeight)

                RenderSystem.frameBuffer.resize(newWidth, newHeight)
                RenderSystem.bloomPass.resize(newWidth, newHeight)

                width = newWidth
                height = newHeight
            }
        }

        glfwSetKeyCallback(handle) { _, key, _, action, _ ->
            if (key == GLFW_KEY_F11 && action == GLFW_PRESS) {
                toggleFullscreen()
            }
        }

        setVSync(true)
        glfwShowWindow(handle)
    }

    fun toggleFullscreen() {
        isFullscreen = !isFullscreen

        if (isFullscreen) {
            val x = IntArray(1)
            val y = IntArray(1)
            glfwGetWindowPos(handle, x, y)
            windowedX = x[0]
            windowedY = y[0]
            windowedWidth = width
            windowedHeight = height

            val monitor = glfwGetPrimaryMonitor()
            val videoMode = glfwGetVideoMode(monitor)

            if (videoMode != null) {
                glfwSetWindowMonitor(
                    handle,
                    monitor,
                    0,
                    0,
                    videoMode.width(),
                    videoMode.height(),
                    videoMode.refreshRate()
                )
            }
        } else {
            glfwSetWindowMonitor(
                handle,
                NULL,
                windowedX,
                windowedY,
                windowedWidth,
                windowedHeight,
                0
            )
        }

        setVSync(true)
    }

    private fun setupGLState() {
        glViewport(0, 0, width, height)
    }

    private fun setupDebugCallback() {
        val flags = glGetInteger(GL_CONTEXT_FLAGS)
        val hasDebug = (flags and GL_CONTEXT_FLAG_DEBUG_BIT) != 0
        log("OpenGL Debug Context: $hasDebug")

        if (!hasDebug) return

        glEnable(GL_DEBUG_OUTPUT)
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS)

        glDebugMessageControl(
            GL_DONT_CARE,
            GL_DONT_CARE,
            GL_DEBUG_SEVERITY_NOTIFICATION,
            null as IntArray?,
            false
        )

        glDebugMessageCallback({ source, type, id, severity, length, message, _ ->

            val msg = GLDebugMessageCallback.getMessage(length, message)

            val sourceStr = when (source) {
                GL_DEBUG_SOURCE_API -> "API"
                GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "WINDOW"
                GL_DEBUG_SOURCE_SHADER_COMPILER -> "SHADER"
                GL_DEBUG_SOURCE_THIRD_PARTY -> "THIRD"
                GL_DEBUG_SOURCE_APPLICATION -> "APP"
                GL_DEBUG_SOURCE_OTHER -> "OTHER"
                else -> "UNKNOWN"
            }

            val typeStr = when (type) {
                GL_DEBUG_TYPE_ERROR -> "ERROR"
                GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> "DEPRECATED"
                GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> "UNDEFINED"
                GL_DEBUG_TYPE_PERFORMANCE -> "PERF"
                GL_DEBUG_TYPE_MARKER -> "MARKER"
                GL_DEBUG_TYPE_OTHER -> "OTHER"
                else -> "UNKNOWN"
            }

            val severityStr = when (severity) {
                GL_DEBUG_SEVERITY_HIGH -> "HIGH"
                GL_DEBUG_SEVERITY_MEDIUM -> "MED"
                GL_DEBUG_SEVERITY_LOW -> "LOW"
                GL_DEBUG_SEVERITY_NOTIFICATION -> "INFO"
                else -> "UNKNOWN"
            }

            if (severity == GL_DEBUG_SEVERITY_NOTIFICATION) return@glDebugMessageCallback

            val formatted = "[OpenGL][$severityStr][$typeStr][$sourceStr][$id] $msg"

            when (severity) {
                GL_DEBUG_SEVERITY_HIGH -> {
                    System.err.println(formatted)
                    Thread.dumpStack()
                }
                GL_DEBUG_SEVERITY_MEDIUM -> System.err.println(formatted)
                else -> println(formatted)
            }

        }, NULL)
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