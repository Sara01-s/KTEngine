package engine.game

import engine.renderer.bindables.Shader
import engine.renderer.bindables.Texture

object Assets : AutoCloseable {

    private val shaders = mutableMapOf<String, Shader>()
    private val textures = mutableMapOf<String, Texture>()

    fun loadText(path: String): String {
        return javaClass.getResource(path)?.readText()
            ?: error("Resource not found: $path")
    }

    fun loadBytes(path: String): ByteArray {
        return javaClass.getResourceAsStream(path)?.readBytes()
            ?: error("Resource not found: $path")
    }

    fun loadShader(vertexShaderPath: String, fragmentShaderPath: String): Shader {
        return shaders.getOrPut(vertexShaderPath + fragmentShaderPath) {
            Shader(
                loadText(vertexShaderPath),
                loadText(fragmentShaderPath)
            )
        }
    }

    fun loadDefaultShader(): Shader {
        return loadShader(
            "/shaders/rect_vsh.glsl",
            "/shaders/rect_fsh.glsl"
        )
    }

    fun loadTexture(path: String): Texture {
        return textures.getOrPut(path) {
            Texture(path)
        }
    }

    override fun close() {
        shaders.values.forEach(Shader::close)
        textures.values.forEach(Texture::close)

        shaders.clear()
        textures.clear()
    }
}