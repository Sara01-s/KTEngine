package engine

import engine.rendering.bindables.FrameBuffer
import engine.rendering.bindables.Material
import engine.rendering.bindables.Shader
import engine.utils.PrimitiveMeshes
import engine.rendering.Window
import org.lwjgl.opengl.GL43.*

class PostProcess(shader: Shader) {
    private val mesh = PrimitiveMeshes.fullScreenQuad
    private val material = Material(shader)

    fun draw(frameBuffer: FrameBuffer) {
        glViewport(0, 0, Window.width, Window.height)
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_BLEND)

        material.setTexture("_ScreenTexture", frameBuffer.textureGpuID, 0)
        material.bind()
        mesh.draw()

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
    }
}