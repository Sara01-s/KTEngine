package engine.renderer

import engine.math.Transform
import engine.math.ortho
import engine.math.scale
import engine.math.translate
import engine.utils.GLDebug.glCall
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import org.lwjgl.opengl.GL11.GL_BLEND
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA
import org.lwjgl.opengl.GL11.GL_SRC_ALPHA
import org.lwjgl.opengl.GL11.glBlendFunc
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL11.glViewport

data class Color(val r: Float, val g: Float, val b: Float, val a: Float = 1.0f) {
    companion object {
        val white = Color(1f, 1f, 1f)
        val black = Color(0f, 0f, 0f)
        val red = Color(1f, 0f, 0f)
        val green = Color(0f, 1f, 0f)
        val blue = Color(0f, 0f, 1f)
        val cyan = Color(0f, 1f, 1f)
        val magenta = Color(1f, 0f, 1f)
        val yellow = Color(1f, 1f, 0f)
        val transparent = Color(0f, 0f, 0f, 0f)
        val coolPurple = Color(0.0667f, 0.0f, 0.0902f)
    }
}

class Renderer {
    companion object {
        fun getMvpMatrix(transform: Transform): Mat4 {
            val aspect = Window.aspectRatio

            val projection = Mat4().identity().ortho(
                -aspect * 10f, aspect * 10f,
                -10f, 10f,
                -10f, 10f
            )

            val view = Mat4().identity()

            val offset = Vec2(
                transform.scale.x * transform.pivot.x,
                transform.scale.y * transform.pivot.y
            )

            val model = Mat4()
                .identity()
                .translate(transform.position.x - offset.x, transform.position.y - offset.y)
                .scale(transform.scale.x, transform.scale.y)

            return projection * view * model
        }
    }

    init {
        glCall {
            glViewport(0, 0, Window.width, Window.height)
            glEnable(GL_BLEND)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        }
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
            glClear(GL_COLOR_BUFFER_BIT)
        }
    }
}