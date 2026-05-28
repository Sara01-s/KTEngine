package engine.rendering.bindables

import engine.utils.GLDebug.glCall
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Mesh(
    val layout: VertexLayout,
    vertexBuffer: ByteBuffer,
    indices: IntArray,
    var material: Material,
    val topology: Int = GL_TRIANGLES,
    val usage: Int = GL_DYNAMIC_DRAW
) : Bindable() {

    companion object {
        private fun buildByteBuffer(layout: VertexLayout, vertices: List<Any>): ByteBuffer {
            val n = layout.elements.size
            val vertexCount = vertices.size / n
            val buffer = ByteBuffer
                .allocateDirect(vertexCount * layout.stride)
                .order(ByteOrder.nativeOrder())

            for (i in 0 until vertexCount) {
                for (j in 0 until n) {
                    val element = layout.elements[j]
                    element.type.write(buffer, i * layout.stride + element.offset, vertices[i * n + j])
                }
            }

            return buffer
        }
    }

    constructor(
        layout: VertexLayout,
        vertices: List<Any>,
        indices: IntArray,
        material: Material,
        topology: Int = GL_TRIANGLES,
        usage: Int = GL_DYNAMIC_DRAW
    ) : this(layout, buildByteBuffer(layout, vertices), indices, material, topology, usage)

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

            for (element in layout.elements) {
                val location = element.type.location

                glEnableVertexAttribArray(location)
                glVertexAttribPointer(
                    /* index = */ location,
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
    fun setData(vertices: List<Any>, indices: IntArray) {
        setData(buildByteBuffer(layout, vertices), indices)
    }

    fun setData(vertices: ByteBuffer, indices: IntArray) {
        if (indices.isEmpty()) {
            indexCount = 0
            return
        }

        glCall {
            glBindVertexArray(vao)
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBufferData(GL_ARRAY_BUFFER, vertices.rewind(), usage)
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