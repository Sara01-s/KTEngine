package engine.systems

import engine.rendering.postprocessing.PostProcess
import engine.assets.Assets
import engine.components.Renderer
import engine.components.Transform
import engine.rendering.Skybox
import engine.rendering.Window
import engine.rendering.bindables.FrameBuffer
import engine.rendering.postprocessing.BloomPass
import engine.utils.Color
import engine.utils.PrimitiveMeshes
import glm_.glm
import glm_.mat4x4.Mat4
import org.lwjgl.opengl.GL43.*

object RenderSystem {
    private val renderers = mutableListOf<Renderer>()
    private val overlayCallbacks = mutableListOf<() -> Unit>()
    private val lhToRh = Mat4(
        1f,  0f,  0f,  0f,
        0f,  1f,  0f,  0f,
        0f,  0f, -1f,  0f,
        0f,  0f,  0f,  1f
    )

    private val postProcess: PostProcess
    val bloomPass: BloomPass
    val frameBuffer: FrameBuffer

    var skybox: Skybox? = null

    init {
        PrimitiveMeshes.fullScreenQuad

        frameBuffer = FrameBuffer(Window.width, Window.height, hdr = true)
        bloomPass = BloomPass(Window.width, Window.height)
        postProcess = PostProcess(Assets.loadShader("shaders/postprocess/shd_post_process.glsl"))

        glViewport(0, 0, Window.width, Window.height)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_MULTISAMPLE)
        glEnable(GL_SAMPLE_ALPHA_TO_COVERAGE)

        setClearColor(Color.gray20)
    }

    fun addOverlay(callback: () -> Unit) {
        overlayCallbacks.add(callback)
    }

    private fun applySceneState() {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
    }

    private fun applyPostProcessState() {
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_BLEND)
    }

    fun render() {
        frameBuffer.bind()
        applySceneState()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glViewport(0, 0, Window.width, Window.height)

        val camera = CameraSystem.main
        if (camera != null) {
            skybox?.draw(
                calculateViewMatrix(camera.entity.transform),
                calculateProjectionMatrix(camera.fov, camera.near, camera.far)
            )

            for (renderer in renderers) {
                if (renderer.isVisible) {
                    renderer.draw()
                }
            }
        }

        frameBuffer.unbind()

        val bloomTextureID = bloomPass.process(frameBuffer.textureGpuID)

        applyPostProcessState()

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        glViewport(0, 0, Window.width, Window.height)
        glClear(GL_COLOR_BUFFER_BIT)

        postProcess.draw(frameBuffer.textureGpuID, bloomTextureID)

        overlayCallbacks.forEach { it.invoke() }
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