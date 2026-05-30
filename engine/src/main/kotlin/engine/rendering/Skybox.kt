package engine.rendering

import engine.assets.Assets
import engine.assets.EngineAssets
import glm_.mat4x4.Mat4
import org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray
import org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays
import org.lwjgl.opengl.GL43.*

class Skybox : AutoCloseable {

    private val skyboxShader = Assets.loadShader("engine_assets/shaders/shd_skybox.glsl")
    private val vao: Int
    private val vbo: Int
    private val ebo: Int

    private val cubeMap = EngineAssets.loadCubeMap(arrayOf(
        "textures/tex_skybox_posx.jpg",
        "textures/tex_skybox_negx.jpg",
        "textures/tex_skybox_posy.jpg",
        "textures/tex_skybox_negy.jpg",
        "textures/tex_skybox_posz.jpg",
        "textures/tex_skybox_negz.jpg",
    ))

    init {
        val vertices = floatArrayOf(
            -1f, -1f, -1f, // 0: left  bottom back
            1f, -1f, -1f, // 1: right bottom back
            1f,  1f, -1f, // 2: right top    back
            -1f,  1f, -1f, // 3: left  top    back
            -1f, -1f,  1f, // 4: left  bottom front
            1f, -1f,  1f, // 5: right bottom front
            1f,  1f,  1f, // 6: right top    front
            -1f,  1f,  1f, // 7: left  top    front
        )

        val indices = intArrayOf(
            // back   (-Z)
            0, 2, 1,  0, 3, 2,
            // front  (+Z)
            4, 5, 6,  4, 6, 7,
            // left   (-X)
            0, 4, 7,  0, 7, 3,
            // right  (+X)
            1, 2, 6,  1, 6, 5,
            // bottom (-Y)
            0, 1, 5,  0, 5, 4,
            // top    (+Y)
            3, 7, 6,  3, 6, 2,
        )

        vao = glGenVertexArrays()
        vbo = glGenBuffers()
        ebo = glGenBuffers()

        glBindVertexArray(vao)

        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.SIZE_BYTES, 0)

        glBindVertexArray(0)
    }

    fun draw(viewMatrix: Mat4, projectionMatrix: Mat4) {
        glDepthFunc(GL_LEQUAL)
        glDisable(GL_CULL_FACE)

        skyboxShader.bind()
        skyboxShader.setUniform("_ViewMatrix", viewMatrix)
        skyboxShader.setUniform("_ProjectionMatrix", projectionMatrix)
        skyboxShader.setUniform("_SkyboxTex", cubeMap, slot = 0)

        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)

        glEnable(GL_CULL_FACE)
        glDepthFunc(GL_LESS)
    }

    override fun close() {
        glDeleteBuffers(vbo)
        glDeleteBuffers(ebo)
        glDeleteVertexArrays(vao)
    }
}