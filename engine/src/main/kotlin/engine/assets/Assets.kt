package engine.assets

import engine.Application
import engine.game.Model
import engine.rendering.bindables.CubeMap
import engine.rendering.bindables.Shader
import engine.rendering.bindables.Texture
import engine.rendering.text.Font
import engine.utils.AudioClip
import engine.utils.Color
import engine.utils.log
import java.nio.file.Path

object Assets : AutoCloseable {
    private val shaders = AssetCache<Shader>()
    private val textures = AssetCache<Texture>()
    private val audioClips = AssetCache<AudioClip>()
    private val models = AssetCache<Model>()
    private val cubeMaps = AssetCache<CubeMap>()

    init {
        log("Assets path: ${Application.assetsPath}")
    }

    private fun resolve(path: String): Path {
        return Application.assetsPath.resolve(AssetUtils.normalize(path))
    }

    fun loadText(path: String): String {
        return AssetUtils.loadText(resolve(path))
    }

    fun loadBytes(path: String): ByteArray {
        return AssetUtils.loadBytes(resolve(path))
    }

    fun loadShader(path: String): Shader {
        return shaders.get(path) {
            log("Loading shader: $path")
            Shader(loadText(path), path)
        }
    }

    fun loadTexture(path: String): Texture {
        return textures.get(path) {
            log("Loading texture: $path")
            Texture(AssetUtils.absolutePath(resolve(path)))
        }
    }

    fun loadAudioClip(path: String): AudioClip {
        return audioClips.get(path) {
            log("Loading audio clip: $path")
            AudioClip(AssetUtils.absolutePath(resolve(path)))
        }
    }

    fun loadModel(path: String): Model {
        return models.get(path) {
            log("Loading model: $path")
            Model(AssetUtils.absolutePath(resolve(path)))
        }
    }

    fun loadCubeMap(paths: Array<String>): CubeMap {
        val resolvedPaths = paths.map { path ->
            AssetUtils.absolutePath(resolve(path))
        }.toTypedArray()

        val key = resolvedPaths.joinToString("|")

        return cubeMaps.get(key) {
            log("Loading cube map")
            CubeMap(resolvedPaths)
        }
    }

    fun loadWhiteTexture(): Texture {
        return textures.get("__white_texture__") {
            Texture.createSolid(Color.white)
        }
    }

    fun loadFont(texturePath: String, structurePath: String): Font {
        return Font.loadFont(
            loadTexture(texturePath),
            loadText(structurePath)
        )
    }

    override fun close() {
        log("Disposing assets cache...")

        shaders.dispose()
        textures.dispose()
        audioClips.dispose()
        models.dispose()
        cubeMaps.dispose()
    }
}