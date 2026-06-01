package engine.rendering.bindables

import engine.assets.AssetUtils
import org.lwjgl.opengl.GL43.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack

class CubeMap(paths: Array<String>) : Bindable(), AutoCloseable {
    init {
        if (paths.size != 6) {
            error("A skybox must have 6 faces")
        }

        gpuID = glGenTextures()
        glBindTexture(GL_TEXTURE_CUBE_MAP, gpuID)

        stbi_set_flip_vertically_on_load(false)

        for (i in 0 until 6) {
            MemoryStack.stackPush().use { stack ->
                val width = stack.mallocInt(1)
                val height = stack.mallocInt(1)
                val channel = stack.mallocInt(1)

                val imageBuffer = AssetUtils.loadByteBuffer(paths[i], this)

                val data = stbi_load_from_memory(
                    imageBuffer,
                    width,
                    height,
                    channel,
                    4
                ) ?: error(
                    "Error while loading face: ${paths[i]} - ${stbi_failure_reason()}"
                )

                val currentSide = GL_TEXTURE_CUBE_MAP_POSITIVE_X + i
                glTexImage2D(
                    /* target = */ currentSide,
                    /* level = */ 0,
                    /* internalformat = */ GL_RGBA,
                    /* width = */ width.get(0),
                    /* height = */ height.get(0),
                    /* border = */ 0,
                    /* format = */ GL_RGBA,
                    /* type = */ GL_UNSIGNED_BYTE,
                    /* pixels = */ data
                )

                stbi_image_free(data)
            }
        }

        stbi_set_flip_vertically_on_load(true)

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)
    }

    fun bind(slot: Int) {
        glActiveTexture(GL_TEXTURE0 + slot)
        glBindTexture(GL_TEXTURE_CUBE_MAP, gpuID)
    }

    override fun bind() {
        bind(0)
    }

    override fun unbind() {
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0)
    }

    override fun close() {
        glDeleteTextures(gpuID)
        unbind()
    }
}