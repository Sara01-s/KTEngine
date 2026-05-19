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

    private val vao: Int = glGenVertexArrays()
    private val vbo: Int = glGenBuffers()
    private val ibo: Int = glGenBuffers()

    var indexCount = indices.size
        private set

    init {
        glCall {
            glBindVertexArray(vao)

            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)

            // Attributes.
            glEnableVertexAttribArray(0)
            glVertexAttribPointer(
                /* index = */ 0,
                /* size = */ 2,
                /* type = */ GL_FLOAT,
                /* normalized = */ false,
                /* stride = */ 4 * Float.SIZE_BYTES,
                /* pointer = */ 0L
            )

            glEnableVertexAttribArray(1)
            glVertexAttribPointer(
                /* index = */ 1,
                /* size = */ 2,
                /* type = */ GL_FLOAT,
                /* normalized = */ false,
                /* stride = */ 4 * Float.SIZE_BYTES,
                /* pointer = */ (2 * Float.SIZE_BYTES).toLong()
            )

            glBindVertexArray(0)
        }

        setData(vertices, indices)
    }

    fun setData(vertices: FloatArray, indices: IntArray) {
        glCall {
            glBindVertexArray(vao)

            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBufferData(GL_ARRAY_BUFFER, vertices, usage)

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, usage)

            glBindVertexArray(0)
        }

        indexCount = indices.size
    }

    fun draw() {
        bind()
        glCall { glDrawElements(topology, indexCount, GL_UNSIGNED_INT, 0L) }
    }

    override fun bind() {
        glCall { glBindVertexArray(vao) }
    }

    override fun unbind() {
        glCall { glBindVertexArray(0) }
    }

    override fun close() {
        glCall {
            glDeleteBuffers(vbo)
            glDeleteBuffers(ibo)
            glDeleteVertexArrays(vao)
        }
    }
}