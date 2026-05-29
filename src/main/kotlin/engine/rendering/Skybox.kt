package engine.rendering

import engine.systems.Assets
import glm_.mat4x4.Mat4
import org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray
import org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays
import org.lwjgl.opengl.GL43.*

class Skybox : AutoCloseable{
    private val skyboxShader = Assets.loadShader("/shaders/shd_skybox.glsl")
    private val vao = -1
    private val vbo = -1

    init {
        val (vao, vbo) = createCubeVAO()
    }

    private val cubeMapTextures = arrayOf(
        "/textures/posx.jpg",
        "/textures/negx.jpg",
        "/textures/negy.jpg",
        "/textures/posy.jpg",
        "/textures/posz.jpg",
        "/textures/negz.jpg",
    )

    private val cubeMap = Assets.loadCubeMap(cubeMapTextures)

    fun draw(viewMatrix: Mat4, projectionMatrix: Mat4) {
        glDepthFunc(GL_LEQUAL)

        skyboxShader.bind()
        skyboxShader.setUniform("_ViewMatrix", viewMatrix)
        skyboxShader.setUniform("_ProjectionMatrix", projectionMatrix)
        skyboxShader.setUniform("_SkyboxTex", cubeMap, slot = 0)

        glBindVertexArray(vao)

        glDisable(GL_CULL_FACE)
        glDrawArrays(GL_TRIANGLES, 0, 36)
        glEnable(GL_CULL_FACE)

        glDepthFunc(GL_LESS)
    }

    private fun createCubeVAO(): Pair<Int, Int> {
        val vertices = floatArrayOf(
            -1f,  1f, -1f, -1f, -1f, -1f,  1f, -1f, -1f,  1f, -1f, -1f,  1f,  1f, -1f, -1f,  1f, -1f,
            -1f, -1f,  1f, -1f, -1f, -1f, -1f,  1f, -1f, -1f,  1f, -1f, -1f,  1f,  1f, -1f, -1f,  1f,
             1f, -1f, -1f,  1f, -1f,  1f,  1f,  1f,  1f,  1f,  1f,  1f,  1f,  1f, -1f,  1f, -1f, -1f,
            -1f, -1f,  1f, -1f,  1f,  1f,  1f,  1f,  1f,  1f,  1f,  1f,  1f, -1f,  1f, -1f, -1f,  1f,
            -1f,  1f, -1f,  1f,  1f, -1f,  1f,  1f,  1f,  1f,  1f,  1f, -1f,  1f,  1f, -1f,  1f, -1f,
            -1f, -1f, -1f, -1f, -1f,  1f,  1f, -1f, -1f,  1f, -1f, -1f, -1f, -1f,  1f,  1f, -1f,  1f
        )

        val vao = glGenVertexArrays()
        val vbo = glGenBuffers()

        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.SIZE_BYTES, 0)

        glBindVertexArray(0)

        return Pair(vao, vbo)
    }

    override fun close() {
        glDeleteBuffers(vbo)
        glDeleteVertexArrays(vao)
    }
}