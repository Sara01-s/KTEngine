package engine.rendering.bindables

import org.lwjgl.opengl.GL43.*
import java.nio.ByteBuffer

class FrameBuffer(var width: Int, var height: Int) : Bindable() {
    var textureGpuID: Int = -1
        private set

    private var renderBufferID: Int = -1

    init {
        setup()
    }

    fun setup() {
        gpuID = glGenFramebuffers()

        glBindFramebuffer(GL_FRAMEBUFFER, gpuID)

        textureGpuID = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, textureGpuID)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, null as ByteBuffer?)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureGpuID, 0)

        renderBufferID = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, renderBufferID)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderBufferID)

        check(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE) { "Incomplete FrameBuffer." }
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun resize(newWidth: Int, newHeight: Int) {
        println("FBO resize: ${newWidth}x${newHeight}")

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