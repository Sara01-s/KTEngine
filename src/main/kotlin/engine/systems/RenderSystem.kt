package engine.systems

import engine.components.Renderer
import engine.components.Transform
import engine.rendering.Skybox
import engine.rendering.Window
import engine.utils.Color
import glm_.glm
import glm_.mat4x4.Mat4
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.GL_MULTISAMPLE

object RenderSystem {
    private val renderers = mutableListOf<Renderer>()

    private val lhToRh = Mat4(
        1f,  0f,  0f,  0f,
        0f,  1f,  0f,  0f,
        0f,  0f, -1f,  0f,
        0f,  0f,  0f,  1f
    )

    var skybox: Skybox? = null

    init {
        glViewport(0, 0, Window.width, Window.height)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_MULTISAMPLE)

        setClearColor(Color.gray30)
    }

    fun render() {
        clearScreen()
        val camera = CameraSystem.main!!
        skybox?.draw(calculateViewMatrix(camera.entity.transform), calculateProjectionMatrix(camera.fov, camera.near, camera.far))

        for (renderer in renderers) {
            if (!renderer.isVisible) {
                continue
            }

            renderer.draw()
        }
    }

    fun register(renderer: Renderer) {
        renderers.add(renderer)
    }

    fun unregister(renderer: Renderer) {
        renderers.remove(renderer)
    }



    fun calculateModelMatrix(transform: Transform): Mat4 {
        return lhToRh * transform.worldMatrix
    }

    fun calculateViewMatrix(cameraTransform: Transform): Mat4 {
        return lhToRh * cameraTransform.worldMatrix.inverse()
    }

    fun calculateProjectionMatrix(fovY: Float, near: Float, far: Float): Mat4 {
        return glm.perspective(
            fovY = fovY,
            aspect = Window.aspectRatio,
            near = near,
            far = far
        )
    }

    fun calculateMvpMatrix(transform: Transform): Mat4 {
        val model = calculateModelMatrix(transform)
        return calculateMvpMatrix(model)
    }

    fun calculateMvpMatrix(modelMatrix: Mat4): Mat4 {
        val camera = CameraSystem.main ?: error("No main camera found.")

        val view = calculateViewMatrix(camera.entity.transform)
        val projection = calculateProjectionMatrix(camera.fov, camera.near, camera.far)

        val mvp = projection * view * modelMatrix

        return mvp
    }

    fun setClearColor(color: Color) {
        setClearColor(color.r, color.g, color.b, color.a)
    }

    fun setClearColor(r: Float, g: Float, b: Float, a: Float = 1f) {
        glClearColor(r, g, b, a)
    }

    fun clearScreen() {
        glViewport(0, 0, Window.width, Window.height)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    fun clear() {
        val safeList = renderers.toList()

        for (renderer in safeList) {
            renderer.close()
        }

        renderers.clear()
    }
}