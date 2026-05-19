package engine.rendering.bindables

import engine.systems.Assets
import engine.utils.GLDebug.glCall
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.glActiveTexture
import org.lwjgl.opengl.GL30.glGenerateMipmap
import org.lwjgl.stb.STBImage.*
import java.nio.ByteBuffer

class Texture(path: String) : Bindable() {

    val width: Int
    val height: Int
    val channels: Int

    init {
        stbi_set_flip_vertically_on_load(true)

        val width = BufferUtils.createIntBuffer(1)
        val height = BufferUtils.createIntBuffer(1)
        val channels = BufferUtils.createIntBuffer(1)

        val bytes = Assets.loadBytes(path)

        val buffer = BufferUtils.createByteBuffer(bytes.size)
        buffer.put(bytes)
        buffer.flip()

        val image: ByteBuffer = stbi_load_from_memory(
            buffer,
            width,
            height,
            channels,
            4
        ) ?: error(
            "Failed to load texture: $path\n${stbi_failure_reason()}"
        )

        this.width = width.get(0)
        this.height = height.get(0)
        this.channels = 4

        glCall {
            gpuID = glGenTextures()

            bind()

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

            glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA8,
                this.width,
                this.height,
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                image
            )

            glGenerateMipmap(GL_TEXTURE_2D)
        }

        stbi_image_free(image)
    }

    fun bind(slot: Int = 0) {
        glCall {
            glActiveTexture(GL_TEXTURE0 + slot)
            glBindTexture(GL_TEXTURE_2D, gpuID)
        }
    }

    override fun bind() {
        bind(0)
    }

    override fun unbind() {
        glCall { glBindTexture(GL_TEXTURE_2D, DEFAULT_GPU_ID) }
    }

    override fun close() {
        glCall { glDeleteTextures(gpuID) }
    }
}