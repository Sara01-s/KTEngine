package engine.systems

import engine.rendering.postprocessing.PostProcess
import engine.assets.Assets
import engine.components.Camera
import engine.components.Renderer
import engine.components.Transform
import engine.rendering.Window
import engine.rendering.bindables.RenderTarget
import engine.scenes.Scene
import engine.utils.Color
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
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

    private val postProcess by lazy { PostProcess(Assets.loadShader("shaders/postprocess/shd_post_process.glsl")) }

    private var uboCamera: Int = 0

    fun init() {
        glViewport(0, 0, Window.width, Window.height)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_MULTISAMPLE)
        glEnable(GL_SAMPLE_ALPHA_TO_COVERAGE)

        setClearColor(Color.gray20)

        SceneSystem.onSceneLoaded.subscribe(::findSceneRenderers)

        val vaoGlobal = glGenVertexArrays()
        glBindVertexArray(vaoGlobal)
        initUbo()
    }

    fun findSceneRenderers(scene: Scene) {
        renderers.clear()

        for (entity in scene.entityMap.values) {
            if (entity.hasComponentOf<Renderer>()) {
                renderers.add(entity.getComponent<Renderer>())
            }
        }
    }

    fun render(camera: Camera, renderTarget: RenderTarget) {
        renderTarget.bind()

        val bg = camera.backgroundColor
        glClearColor(bg.r, bg.g, bg.b, bg.a)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        applySceneState()
        glViewport(0, 0, renderTarget.width, renderTarget.height)

        val aspect = renderTarget.width.toFloat() / renderTarget.height.toFloat()
        val viewMatrix = calculateViewMatrix(camera.entity.transform)
        val projectionMatrix = calculateProjectionMatrix(camera.fov, camera.near, camera.far, aspect)

        updateCameraUBO(viewMatrix, projectionMatrix, camera.entity.transform.worldPosition)

        if (camera.backgroundMode == Camera.BackgroundMode.SkyBox) {
            camera.skybox?.draw(viewMatrix, projectionMatrix)
        }

        for (renderer in renderers) {
            if (renderer.isVisible && camera.shouldRender(renderer.entity)) {
                renderer.draw(camera, aspect)
            }
        }

        renderTarget.unbind()

        glViewport(0, 0, Window.width, Window.height)
    }

    private fun initUbo() {
        uboCamera = glGenBuffers()
        glBindBuffer(GL_UNIFORM_BUFFER, uboCamera)
        glBufferData(GL_UNIFORM_BUFFER, 160, GL_DYNAMIC_DRAW)
        glBindBufferBase(GL_UNIFORM_BUFFER, 0, uboCamera) // 0: binding = 0
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }

    fun updateCameraUBO(view: Mat4, projection: Mat4, cameraPosition: Vec3) {
        if (uboCamera == 0) return

        glBindBuffer(GL_UNIFORM_BUFFER, uboCamera)

        val viewArray = view.toFloatArray()
        val projArray = projection.toFloatArray()

        glBufferSubData(GL_UNIFORM_BUFFER, 0, viewArray)
        glBufferSubData(GL_UNIFORM_BUFFER, 64, projArray)

        val posArray = floatArrayOf(cameraPosition.x, cameraPosition.y, cameraPosition.z, 0f)
        glBufferSubData(GL_UNIFORM_BUFFER, 128, posArray)

        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }

    fun drawPostProcess(renderTarget: RenderTarget) {
        applyPostProcessState()

        renderTarget.unbind()

        glViewport(0, 0, Window.width, Window.height)
        glClear(GL_COLOR_BUFFER_BIT)

        postProcess.draw(renderTarget.textureGpuID)

        overlayCallbacks.forEach { it.invoke() }
    }

    fun drawUI() {
        overlayCallbacks.forEach { it.invoke() }
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

    fun calculateModelMatrix(transform: Transform): Mat4 {
        return lhToRh * transform.worldMatrix
    }

    fun calculateViewMatrix(cameraTransform: Transform): Mat4 {
        return lhToRh * cameraTransform.worldMatrix.inverse()
    }

    fun calculateProjectionMatrix(fovY: Float, near: Float, far: Float, aspect: Float): Mat4 {
        return glm.perspective(fovY, aspect, near, far)
    }

    fun getMvpMatrices(transform: Transform, camera: Camera, aspect: Float) : Triple<Mat4, Mat4, Mat4> {
        val model = calculateModelMatrix(transform)
        val view = calculateViewMatrix(camera.entity.transform)
        val projection = calculateProjectionMatrix(camera.fov, camera.near, camera.far, aspect)

        return Triple(model, view, projection)
    }

    fun calculateMvpMatrix(modelMatrix: Mat4, camera: Camera, aspect: Float): Mat4 {
        val view = calculateViewMatrix(camera.entity.transform)
        val projection = calculateProjectionMatrix(camera.fov, camera.near, camera.far, aspect)
        return projection * view * modelMatrix
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
        SceneSystem.onSceneLoaded.unsubscribeAllFrom(this)
    }
}