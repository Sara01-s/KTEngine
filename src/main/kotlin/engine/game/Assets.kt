package engine.game

import engine.renderer.Color
import engine.renderer.bindables.Material
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

    fun loadDefaultShader(): Shader {
        return loadShader("/shaders/shd_textured.glsl")
    }

    fun loadShader(source: String): Shader {
        return shaders.getOrPut(source) {
            Shader(loadText(source))
        }
    }

    fun loadDefaultTexture(): Texture {
        return loadTexture("/textures/tex_square.png")
    }

    fun loadTexture(path: String): Texture {
        return textures.getOrPut(path) {
            Texture(path)
        }
    }

    fun loadDefaultMaterial(): Material {
        return Material(loadDefaultShader()).apply {
            setTexture("_MainTex", loadDefaultTexture())
            setColor("_ColorTint", Color.white)
        }
    }

    override fun close() {
        shaders.values.forEach(Shader::close)
        textures.values.forEach(Texture::close)

        shaders.clear()
        textures.clear()
    }
}