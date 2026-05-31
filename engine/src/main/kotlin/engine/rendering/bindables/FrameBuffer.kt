package engine.rendering.bindables

import org.lwjgl.opengl.GL43.*
import java.nio.ByteBuffer

class FrameBuffer(var width: Int, var height: Int, val hdr: Boolean = false) : Bindable() {
    var textureGpuID: Int = -1
        private set

    private var renderBufferID: Int = -1

    init {
        setup()
        println("FBO creado con resolución: ${width}x${height}")
    }

    fun setup() {
        gpuID = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, gpuID)

        textureGpuID = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, textureGpuID)

        val internalFormat = if (hdr) GL_RGBA16F else GL_RGB8
        val format = if (hdr) GL_RGBA else GL_RGB
        val dataType = if (hdr) GL_FLOAT else GL_UNSIGNED_BYTE

        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, dataType, null as ByteBuffer?)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureGpuID, 0)

        renderBufferID = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, renderBufferID)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderBufferID)

        val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        check(status == GL_FRAMEBUFFER_COMPLETE) { "Incomplete FrameBuffer. Status: $status" }

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun resize(newWidth: Int, newHeight: Int) {
        glDeleteFramebuffers(gpuID)
        glDeleteTextures(textureGpuID)
        glDeleteRenderbuffers(renderBufferID)

        width = newWidth
        height = newHeight

        setup()
    }

    override fun bind() { glBindFramebuffer(GL_FRAMEBUFFER, gpuID) }
    override fun unbind() { glBindFramebuffer(GL_FRAMEBUFFER, 0) }
    override fun close() {
        glDeleteFramebuffers(gpuID)
        glDeleteTextures(textureGpuID)
        glDeleteRenderbuffers(renderBufferID)
    }
}