package engine.renderer.bindables

import engine.utils.GLDebug.glCall
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW
import org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.glBindBuffer
import org.lwjgl.opengl.GL15.glBufferData
import org.lwjgl.opengl.GL15.glBufferSubData
import org.lwjgl.opengl.GL15.glDeleteBuffers
import org.lwjgl.opengl.GL15.glGenBuffers
import java.nio.IntBuffer

fun IntArray.toIntBuffet(): IntBuffer {
    val buffer = BufferUtils.createIntBuffer(size)

    buffer.put(this)
    buffer.flip()

    return buffer
}

class IndexBuffer(data: IntBuffer) : Bindable() {

    init {
        glCall {
            gpuID = glGenBuffers()
            bind()
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW)
        }
    }

    fun setSubBufferData(data: IntBuffer) {
        glCall {
            bind()
            glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0L, data)
        }
    }

    override fun bind() {
        glCall { glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, gpuID) }
    }

    override fun unbind() {
        glCall { glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, DEFAULT_GPU_ID) }
    }

    override fun close() {
        glCall { glDeleteBuffers(gpuID) }
    }
}