package engine.renderer.bindables

import engine.utils.GLDebug.glCall
import org.lwjgl.opengl.GL30.*

class VertexArrayObject : Bindable() {

    init {
        glCall { gpuID = glGenVertexArrays() }
    }

    fun addAttribute(location: Int, elementType: Int, elementCount: Int, stride: Int, pointer: Long) {
        bind()

        glCall {
            glEnableVertexAttribArray(location)
            glVertexAttribPointer(location, elementCount, elementType, false, stride, pointer)
        }
    }

    override fun bind() {
        glCall { glBindVertexArray(gpuID) }
    }

    override fun unbind() {
        glCall { glBindVertexArray(0) }
    }

    override fun close() {
        glCall { glDeleteVertexArrays(gpuID) }
    }
}