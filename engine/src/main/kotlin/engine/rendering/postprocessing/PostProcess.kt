package engine.rendering.postprocessing

import engine.rendering.Window
import engine.rendering.bindables.Material
import engine.rendering.bindables.Shader
import engine.utils.PrimitiveMeshes
import org.lwjgl.opengl.GL43.*

class PostProcess(shader: Shader) {
    private val mesh = PrimitiveMeshes.quad
    private val material = Material(shader)

    fun draw(sceneTextureID: Int, bloomTextureID: Int) {
        glViewport(0, 0, Window.width, Window.height)
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_BLEND)

        material.setTexture("_ScreenTexture", sceneTextureID, 0)
        material.bind()
        material.setTexture("_BloomTexture", bloomTextureID, 1)

        material.bind()
        mesh.bind()

        glDrawElements(mesh.topology, mesh.indexCount, GL_UNSIGNED_INT, 0L)

        mesh.unbind()

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
    }
}