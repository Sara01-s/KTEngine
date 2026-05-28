package engine.rendering.bindables

import org.lwjgl.opengl.GL43.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack

class CubeMap(val id: Int) : Bindable(), AutoCloseable {

    constructor(paths: Array<String>) : this(glGenTextures()) {
        if (paths.size != 6) {
            error("A skybox must have 6 faces")
        }

        glBindTexture(GL_TEXTURE_CUBE_MAP, id)

        stbi_set_flip_vertically_on_load(true)

        for (i in 0 until 6) {
            MemoryStack.stackPush().use { stack ->
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                val comp = stack.mallocInt(1)

                val data = stbi_load(paths[i], w, h, comp, 4)
                    ?: error("Error while loading face: ${paths[i]} - ${stbi_failure_reason()}")

                glTexImage2D(
                    GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                    0, GL_RGBA8, w.get(0), h.get(0), 0,
                    GL_RGBA, GL_UNSIGNED_BYTE, data
                )

                stbi_image_free(data)
            }
        }

        stbi_set_flip_vertically_on_load(false)

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)
    }

    fun bind(slot: Int) {
        glActiveTexture(GL_TEXTURE0 + slot)
        glBindTexture(GL_TEXTURE_CUBE_MAP, id)
    }

    override fun bind() {
        bind(0)
    }

    override fun unbind() {
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0)
    }

    override fun close() {
        glDeleteTextures(id)
        unbind()
    }
}