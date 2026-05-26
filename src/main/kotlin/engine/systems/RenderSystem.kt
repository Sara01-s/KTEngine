package engine.systems

import engine.components.Renderer
import engine.components.Transform
import engine.rendering.Window
import engine.utils.Color
import engine.utils.GLDebug.glCall
import glm_.glm
import glm_.mat4x4.Mat4
import org.lwjgl.opengl.GL11.*

object RenderSystem {
    private val renderers = mutableListOf<Renderer>()

    private val lhToRh = Mat4(
        1f,  0f,  0f,  0f,
        0f,  1f,  0f,  0f,
        0f,  0f, -1f,  0f,
        0f,  0f,  0f,  1f
    )

    fun register(renderer: Renderer) {
        renderers.add(renderer)
    }

    fun unregister(renderer: Renderer) {
        renderers.remove(renderer)
    }

    fun render() {
        for (renderer in renderers) {
            if (!renderer.isVisible) {
                continue
            }

            renderer.draw()
        }
    }

    init {
        glCall {
            glViewport(0, 0, Window.width, Window.height)
            glEnable(GL_BLEND)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            glEnable(GL_DEPTH_TEST)
        }

        setClearColor(Color.gray30)
    }

    fun calculateModelMatrix(transform: Transform): Mat4 {
        return lhToRh * transform.worldMatrix
    }

    fun calculateViewMatrix(cameraTransform: Transform): Mat4 {
        return lhToRh * cameraTransform.worldMatrix.inverse()
    }

    fun calculateMvpMatrix(transform: Transform): Mat4 {
        val camera = CameraSystem.main

        val model = lhToRh * transform.worldMatrix

        val view  = calculateViewMatrix(camera!!.entity.transform)

        val projection = glm.perspective(
            fovY = 45f,
            aspect = Window.aspectRatio,
            near = 0.01f,
            far = 1000f,
        )

        return projection * view * model
    }

    fun setClearColor(color: Color) {
        setClearColor(color.r, color.g, color.b, color.a)
    }

    fun setClearColor(r: Float, g: Float, b: Float, a: Float = 1f) {
        glCall { glClearColor(r, g, b, a) }
    }

    fun clearScreen() {
        glCall {
            glViewport(0, 0, Window.width, Window.height)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        }
    }
}