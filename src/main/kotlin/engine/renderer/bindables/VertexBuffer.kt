package engine.renderer.bindables

import engine.renderer.Color
import engine.utils.GLDebug.glCall
import glm_.vec2.Vec2
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW
import org.lwjgl.opengl.GL15.glBindBuffer
import org.lwjgl.opengl.GL15.glBufferData
import org.lwjgl.opengl.GL15.glGenBuffers
import org.lwjgl.opengl.GL15.glBufferSubData
import org.lwjgl.opengl.GL15.glDeleteBuffers
import java.nio.ByteBuffer

data class Vertex(
    val position: Vec2,
    val texCoord: Vec2,
) {
    companion object {
        const val SIZE_BYTES = (2 + 2) * Float.SIZE_BYTES
    }

    fun putIn(buffer: ByteBuffer) {
        buffer.putFloat(position.x)
        buffer.putFloat(position.y)

        buffer.putFloat(texCoord.x)
        buffer.putFloat(texCoord.y)
    }
}

fun Array<Vertex>.toByteBuffer(): ByteBuffer {
    val buffer = BufferUtils.createByteBuffer(size * Vertex.SIZE_BYTES)

    forEach { it.putIn(buffer) }
    buffer.flip()

    return buffer
}

class VertexBuffer(data: ByteBuffer) : Bindable() {

    init {
        glCall {
            gpuID = glGenBuffers()
            bind()
            glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW)
        }
    }

    fun setSubBufferData(data: ByteBuffer) {
        bind()
        glCall { glBufferSubData(GL_ARRAY_BUFFER, 0L, data) }
    }

    override fun bind() {
        glCall { glBindBuffer(GL_ARRAY_BUFFER, gpuID) }
    }

    override fun unbind() {
        glCall { glBindBuffer(GL_ARRAY_BUFFER, DEFAULT_GPU_ID) }
    }

    override fun close() {
        glCall { glDeleteBuffers(gpuID) }
    }

}