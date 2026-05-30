package engine.assets

import engine.game.Model
import engine.rendering.bindables.*
import engine.utils.log
import engine.rendering.text.Font
import java.io.InputStream
import engine.assets.AssetUtils

object EngineAssets {
    private const val ROOT = "/engine_assets"

    init {
        log("Engine assets initialized at: $ROOT")
    }

    private fun resolve(path: String): String {
        return "$ROOT/${AssetUtils.normalize(path)}"
    }

    fun loadShader(path: String): Shader {
        return Shader(AssetUtils.loadResourceText(this, resolve(path)))
    }

    fun loadTexture(path: String): Texture {
        val bytes = AssetUtils.loadResourceBytes(this, resolve(path))

        val buffer = java.nio.ByteBuffer.allocateDirect(bytes.size).apply {
            put(bytes)
            flip()
        }

        return Texture.fromMemory(buffer)
    }

    fun loadModel(path: String): Model {
        val tempFile = AssetUtils.createTempFileFromResource(this, resolve(path))
        return Model(tempFile)
    }

    fun loadFont(texturePath: String, structurePath: String): Font {
        val texture = loadTexture(texturePath)
        val structure = loadText(structurePath)

        return Font.loadFont(texture, structure)
    }

    fun loadCubeMap(paths: Array<String>): CubeMap {
        val resolvedPaths = paths.map { path ->
            resolve(path)
        }.toTypedArray()

        return CubeMap(resolvedPaths)
    }

    fun exists(path: String): Boolean = javaClass.getResource(resolve(path)) != null
    fun loadText(path: String): String = AssetUtils.loadResourceText(this, resolve(path))
    fun loadBytes(path: String): ByteArray = AssetUtils.loadResourceBytes(this, resolve(path))
    fun loadStream(path: String): InputStream = AssetUtils.loadResourceStream(this, resolve(path))
}