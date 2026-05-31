package engine.rendering.bindables

import engine.assets.Assets
import engine.utils.Color
import org.lwjgl.BufferUtils
import org.lwjgl.assimp.AITexel
import org.lwjgl.opengl.EXTTextureFilterAnisotropic
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.glActiveTexture
import org.lwjgl.opengl.GL30.glGenerateMipmap
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import kotlin.error

class Texture private constructor() : Bindable() {
    var width: Int = 1
        private set

    var height: Int = 1
        private set

    var channels: Int = 4
        private set

    constructor(existingGpuID: Int, width: Int, height: Int) : this() {
        this.gpuID = existingGpuID
        this.width = width
        this.height = height
    }

    companion object {
        operator fun invoke(path: String): Texture {
            val tex = Texture()
            tex.loadFromPath(path)
            return tex
        }

        fun createEmpty(): Texture {
            return Texture().apply {
                gpuID = glGenTextures()
            }
        }

        fun createSolid(color: Color): Texture {
            val tex = Texture()
            tex.loadSolid(color)
            return tex
        }

        fun fromMemory(data: ByteBuffer): Texture {
            MemoryStack.stackPush().use { stack ->

                val width = stack.mallocInt(1)
                val height = stack.mallocInt(1)
                val channels = stack.mallocInt(1)

                val image = stbi_load_from_memory(
                    data,
                    width,
                    height,
                    channels,
                    4
                ) ?: error(stbi_failure_reason().toString())

                val texture = createEmpty()

                texture.width = width[0]
                texture.height = height[0]
                texture.channels = 4

                glBindTexture(GL_TEXTURE_2D, texture.gpuID)

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

                val maxAnisotropy = glGetFloat(
                    EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT
                )

                glTexParameterf(
                    GL_TEXTURE_2D,
                    EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                    maxAnisotropy
                )

                glTexImage2D(
                    GL_TEXTURE_2D,
                    0,
                    GL_RGBA8,
                    texture.width,
                    texture.height,
                    0,
                    GL_RGBA,
                    GL_UNSIGNED_BYTE,
                    image
                )

                glGenerateMipmap(GL_TEXTURE_2D)

                stbi_image_free(image)

                return texture
            }
        }

        fun fromAssimpRGBA(
            width: Int,
            height: Int,
            texels: AITexel.Buffer
        ): Texture {

            val texture = createEmpty()
            val buffer = BufferUtils.createByteBuffer(width * height * 4)

            for (i in 0 until width * height) {

                val texel = texels[i]

                buffer.put(texel.r())
                buffer.put(texel.g())
                buffer.put(texel.b())
                buffer.put(texel.a())
            }

            buffer.flip()

            texture.width = width
            texture.height = height
            texture.channels = 4

            glBindTexture(GL_TEXTURE_2D, texture.gpuID)

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

            val maxAnisotropy = glGetFloat(
                EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT
            )

            glTexParameterf(
                GL_TEXTURE_2D,
                EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                maxAnisotropy
            )

            glTexImage2D(
                /* target = */ GL_TEXTURE_2D,
                /* level = */ 0,
                /* internalformat = */ GL_RGBA8,
                /* width = */ width,
                /* height = */ height,
                /* border = */ 0,
                /* format = */ GL_RGBA,
                /* type = */ GL_UNSIGNED_BYTE,
                /* pixels = */ buffer
            )

            glGenerateMipmap(GL_TEXTURE_2D)

            return texture
        }
    }

    private fun loadFromPath(path: String) {
        stbi_set_flip_vertically_on_load(true)

        val w = BufferUtils.createIntBuffer(1)
        val h = BufferUtils.createIntBuffer(1)
        val c = BufferUtils.createIntBuffer(1)

        val file = java.io.File(path)

        val bytes: ByteArray =
            if (file.exists()) {
                file.readBytes()
            } else {

                val resourcePath =
                    if (path.startsWith("/"))
                        path
                    else
                        "/$path"

                try {
                    Assets.loadBytes(resourcePath)
                } catch (_: Exception) {
                    error("Texture not found neither in resources or absolute path: $path")
                }
            }

        val buffer = BufferUtils.createByteBuffer(bytes.size).apply {
            put(bytes)
            flip()
        }

        val image: ByteBuffer = stbi_load_from_memory(buffer, w, h, c, 4)
            ?: error("Failed to load texture: $path\n${stbi_failure_reason()}")

        width = w[0]
        height = h[0]
        channels = 4

        gpuID = glGenTextures()

        glBindTexture(GL_TEXTURE_2D, gpuID)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        val maxAnisotropy = glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT)
        glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy)

        glTexImage2D(
            /* target = */ GL_TEXTURE_2D,
            /* level = */ 0,
            /* internalformat = */ GL_RGBA8,
            /* width = */ width,
            /* height = */ height,
            /* border = */ 0,
            /* format = */ GL_RGBA,
            /* type = */ GL_UNSIGNED_BYTE,
            /* pixels = */ image
        )

        glGenerateMipmap(GL_TEXTURE_2D)

        stbi_image_free(image)
    }

    private fun loadSolid(color: Color) {
        val pixel = ByteBuffer.allocateDirect(4).apply {
            put((color.r * 255f).toInt().toByte())
            put((color.g * 255f).toInt().toByte())
            put((color.b * 255f).toInt().toByte())
            put((color.a * 255f).toInt().toByte())
            flip()
        }

        gpuID = glGenTextures()

        glBindTexture(GL_TEXTURE_2D, gpuID)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        glTexImage2D(
            /* target = */ GL_TEXTURE_2D,
            /* level = */ 0,
            /* internalformat = */ GL_RGBA8,
            /* width = */ 1,
            /* height = */ 1,
            /* border = */ 0,
            /* format = */ GL_RGBA,
            /* type = */ GL_UNSIGNED_BYTE,
            /* pixels = */ pixel
        )
    }

    fun bind(slot: Int = 0) {
        glActiveTexture(GL_TEXTURE0 + slot)
        glBindTexture(GL_TEXTURE_2D, gpuID)
    }

    override fun bind() {
        bind(0)
    }

    override fun unbind() {
        glBindTexture(GL_TEXTURE_2D, DEFAULT_GPU_ID)
    }

    override fun close() {
        glDeleteTextures(gpuID)
    }
}