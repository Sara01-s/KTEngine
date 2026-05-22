package engine.systems

import engine.components.Renderer
import engine.components.Transform
import engine.game.Camera
import engine.utils.scale
import engine.utils.translate
import engine.rendering.Window
import engine.utils.Color
import engine.utils.GLDebug.glCall
import engine.utils.makeFrustum
import engine.utils.ortho
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import org.lwjgl.opengl.GL11.*

object RenderSystem {
    private val renderers = mutableListOf<Renderer>()

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
        }

        setClearColor(Color.coolPurple)
    }

    fun calculateMvpMatrix(transform: Transform): Mat4 {
        val aspect = Window.aspectRatio

        val projection = Mat4().identity().ortho(
            left = -aspect * 10f,
            right = aspect * 10f,
            bottom = -10f,
            top = 10f,
            near = -10f,
            far = 10f
        )

        //val projection = Mat4().identity().makeFrustum(
        //    fovYDegrees = 45.0f,
        //    aspectRatio = aspect,
        //    near = 0.01f,
        //    far = 1000.0f
        //)

        val view = Mat4().identity()

        val model = Mat4()
            .identity()
            .translate(transform.position.x, transform.position.y, transform.position.z)
            .scale(transform.scale.x, transform.scale.y, transform.scale.z)

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
            glClear(GL_COLOR_BUFFER_BIT)
        }
    }
}