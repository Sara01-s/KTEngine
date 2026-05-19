package engine.rendering.bindables

import engine.utils.GLDebug.glCall
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30.*

class Mesh(
    vertices: FloatArray,
    indices: IntArray,
    val topology: Int = GL_TRIANGLES,
    val usage: Int = GL_STATIC_DRAW
) : Bindable() {
    companion object {
        fun generateQuad() : Mesh {
            return Mesh(
                vertices = floatArrayOf(
                    /*pos*/ -0.5f, -0.5f, /*uv*/ 0f, 0f,
                    /*pos*/  0.5f, -0.5f, /*uv*/ 1f, 0f,
                    /*pos*/  0.5f,  0.5f, /*uv*/ 1f, 1f,
                    /*pos*/ -0.5f,  0.5f, /*uv*/ 0f, 1f
                ),
                indices = intArrayOf(0, 1, 2, 2, 3, 0)
            )
        }
    }

    private val vao: Int = glGenVertexArrays()
    private val vbo: Int = glGenBuffers()
    private val ibo: Int = glGenBuffers()

    val indexCount = indices.size

    init {
        glCall {
            glBindVertexArray(vao)

            // Vertex buffer.
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBufferData(GL_ARRAY_BUFFER, vertices, usage)

            // Index buffer.
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, usage)

            // Position.
            glEnableVertexAttribArray(0)
            glVertexAttribPointer(
                0,
                2,
                GL_FLOAT,
                false,
                4 * Float.SIZE_BYTES,
                0L
            )

            // TexCoords.
            glEnableVertexAttribArray(1)
            glVertexAttribPointer(
                1,
                2,
                GL_FLOAT,
                false,
                4 * Float.SIZE_BYTES,
                (2 * Float.SIZE_BYTES).toLong()
            )

            glBindVertexArray(0)
        }
    }

    fun draw() {
        bind()
        glCall { glDrawElements(topology, indexCount, GL_UNSIGNED_INT, 0L) }
    }

    override fun bind() {
        glCall { glBindVertexArray(vao) }
    }

    override fun unbind() {
        glCall { glBindVertexArray(DEFAULT_GPU_ID) }
    }

    override fun close() {
        glCall {
            glDeleteBuffers(vbo)
            glDeleteBuffers(ibo)
            glDeleteVertexArrays(vao)
        }
    }
}