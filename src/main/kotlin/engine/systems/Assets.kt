package engine.systems

import engine.rendering.bindables.Material
import engine.rendering.bindables.Shader
import engine.rendering.bindables.Texture
import engine.rendering.text.Font
import engine.utils.AudioClip
import engine.utils.Color

object Assets : AutoCloseable {

    private val shaders = mutableMapOf<String, Shader>()
    private val textures = mutableMapOf<String, Texture>()
    private val audioClips = mutableMapOf<String, AudioClip>()

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

    fun loadAudioClip(path: String): AudioClip {
        return audioClips.getOrPut(path) {
            AudioClip(path)
        }
    }

    fun loadDefaultMaterial(): Material {
        return Material(loadDefaultShader()).apply {
            setTexture("_MainTex", loadDefaultTexture())
            setColor("_ColorTint", Color.white)
        }
    }

    fun loadDefaultTextMaterial(): Material {
        return Material(loadShader("/shaders/shd_font.glsl")).apply {
            setColor("_ColorTint", Color.white)
        }
    }

    fun loadDefaultFont(): Font {
        return loadFont(
            "/fonts/font_tex_pixelated.png",
            "/fonts/font_structure_pixelated.json"
        )
    }

    fun loadFont(texturePath: String, structurePath: String): Font {
        return Font.loadFont(
            loadTexture(texturePath),
            loadText(structurePath)
        )
    }

    override fun close() {
        shaders.values.forEach(Shader::close)
        textures.values.forEach(Texture::close)
        audioClips.values.forEach(AudioClip::close)

        shaders.clear()
        textures.clear()
        audioClips.clear()
    }
}