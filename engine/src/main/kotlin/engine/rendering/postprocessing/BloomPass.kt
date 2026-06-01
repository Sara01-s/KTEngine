package engine.rendering.postprocessing

import engine.assets.Assets
import engine.rendering.bindables.RenderTarget
import engine.utils.PrimitiveMeshes
import org.lwjgl.opengl.GL43.*

class BloomPass(width: Int, height: Int) {
    private val resW = width
    private val resH = height

    val bloomFBO = RenderTarget(resW, resH, hdr = true)
    private val pingPongFBO = Array(2) { RenderTarget(resW, resH, hdr = true) }

    private val extractShader = Assets.loadShader("shaders/postprocess/bloom/shd_bloom_extract.glsl")
    private val blurShader = Assets.loadShader("shaders/postprocess/bloom/shd_bloom_blur.glsl")

    private val quad = PrimitiveMeshes.quad

    fun process(sceneTextureID: Int): Int {
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_BLEND)

        bloomFBO.bind()
        glViewport(0, 0, resW, resH)
        extractShader.bind()

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, sceneTextureID)
        extractShader.setUniform("_SceneTexture", 0)

        quad.bind()
        glDrawElements(quad.topology, quad.indexCount, GL_UNSIGNED_INT, 0L)
        quad.unbind()
        bloomFBO.unbind()

        return bloomFBO.textureGpuID
    }

    fun resize(width: Int, height: Int) {
        bloomFBO.resize(width, height)
    }
}