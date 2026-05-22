package engine.rendering.bindables

import engine.utils.GLDebug.glCall
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30.*
import java.nio.ByteBuffer

class Mesh(
    vertexBuffer: ByteBuffer,
    val indices: IntArray,
    val layout: VertexLayout,
    val topology: Int = GL_TRIANGLES,
    val usage: Int = GL_DYNAMIC_DRAW
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

            for ((index, element) in layout.elements.withIndex()) {
                glEnableVertexAttribArray(index)
                glVertexAttribPointer(
                    /* index = */ index,
                    /* size = */ element.type.length,
                    /* type = */ element.type.glType,
                    /* normalized = */ false,
                    /* stride = */ layout.stride,
                    /* pointer = */ element.offset.toLong()
                )
            }

            glBindVertexArray(0)
        }

        setData(vertexBuffer, indices)
    }

    fun setData(vertices: ByteBuffer, indices: IntArray) {
        if (vertices.remaining() == 0 || indices.isEmpty()) {
            indexCount = 0
            return
        }

        vertices.rewind()

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
        if (indexCount <= 0) {
            return
        }

        bind()
        glCall { glDrawElements(topology, indexCount, GL_UNSIGNED_INT, 0L) }
    }

    override fun bind() { glCall { glBindVertexArray(vao) } }
    override fun unbind() { glCall { glBindVertexArray(0) } }

    override fun close() {
        glCall {
            glDeleteBuffers(vbo)
            glDeleteBuffers(ibo)
            glDeleteVertexArrays(vao)
        }
    }
}