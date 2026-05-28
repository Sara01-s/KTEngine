package engine.systems

import engine.game.Model
import engine.rendering.bindables.Material
import engine.rendering.bindables.Shader
import engine.rendering.bindables.Texture
import engine.rendering.text.Font
import engine.utils.AudioClip
import engine.utils.Color
import engine.rendering.bindables.CubeMap
import org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT
import org.lwjgl.stb.STBImage.stbi_failure_reason
import org.lwjgl.stb.STBImage.stbi_load_from_memory
import org.lwjgl.system.MemoryStack
import org.lwjgl.opengl.GL43.*
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load
import java.io.File
import java.nio.ByteBuffer
import kotlin.io.path.createTempDirectory
import org.lwjgl.system.MemoryUtil

object Assets : AutoCloseable {

    private val shaders = mutableMapOf<String, Shader>()
    private val textures = mutableMapOf<String, Texture>()
    private val audioClips = mutableMapOf<String, AudioClip>()
    private val modelsPaths = mutableMapOf<String, String>()
    private val models = mutableMapOf<String, Model>()
    private val cubeMaps = mutableMapOf<String, CubeMap>()

    fun loadText(path: String): String {
        return javaClass.getResource(path)?.readText()
            ?: error("Resource not found: $path")
    }

    fun loadBytes(path: String): ByteArray {
        return javaClass.getResourceAsStream(path)?.readBytes()
            ?: error("Resource not found: $path")
    }

    fun resolveTexturePath(path: String): String {
        val fileName = path.substringAfterLast('/')
        val prefix = fileName.substringBeforeLast('.')
        val extension = "." + fileName.substringAfterLast('.')

        val tempFile = File.createTempFile(prefix, extension)
        tempFile.deleteOnExit()

        javaClass.getResourceAsStream(path)?.use { input ->
            tempFile.outputStream().use(input::copyTo)
        } ?: error("Resource not found: $path")

        return tempFile.absolutePath
    }

    fun resolveModelResourcePath(path: String): String {
        return modelsPaths.getOrPut(path) {
            val sanitizedPath = path.replace('\\', '/')
            val extension = sanitizedPath.substringAfterLast('.')
            val tempDir = createTempDirectory("model_import_").toFile()

            tempDir.deleteOnExit()

            val modelFile = tempDir.resolve(
                sanitizedPath.substringAfterLast('/')
            )

            modelFile.deleteOnExit()

            javaClass.getResourceAsStream(sanitizedPath)?.use { input ->
                modelFile.outputStream().use(input::copyTo)
            } ?: error("Resource not found: $path")

            val resourceFolder = sanitizedPath.substringBeforeLast('/', "")

            extractSiblingResources(resourceFolder, tempDir, extension)

            modelFile.absolutePath
        }
    }

    private fun extractSiblingResources(
        resourceFolder: String,
        tempDir: File,
        extension: String
    ) {
        when (extension.lowercase()) {
            "obj" -> {
                extractObjDependencies(resourceFolder, tempDir)
            }
            "fbx" -> {
                extractFbxDependencies(resourceFolder, tempDir)
            }
        }
    }

    private fun extractObjDependencies(
        resourceFolder: String,
        tempDir: File
    ) {
        val resourceUrl = javaClass.getResource(resourceFolder) ?: return
        val folder = File(resourceUrl.toURI())

        folder.listFiles()?.forEach { file ->
            val name = file.name.lowercase()

            val valid =
                name.endsWith(".mtl") ||
                name.endsWith(".png") ||
                name.endsWith(".jpg") ||
                name.endsWith(".jpeg") ||
                name.endsWith(".tga") ||
                name.endsWith(".bmp")

            if (!valid)
                return@forEach

            val target = tempDir.resolve(file.name)

            file.inputStream().use { input ->
                target.outputStream().use(input::copyTo)
            }

            target.deleteOnExit()
        }
    }

    private fun extractFbxDependencies(
        resourceFolder: String,
        tempDir: File
    ) {
        val resourceUrl = javaClass.getResource(resourceFolder) ?: return
        val folder = File(resourceUrl.toURI())

        folder.walkTopDown().forEach { file ->
            if (!file.isFile)
                return@forEach

            val name = file.name.lowercase()
            val valid =
                name.endsWith(".png") ||
                name.endsWith(".jpg") ||
                name.endsWith(".jpeg") ||
                name.endsWith(".tga") ||
                name.endsWith(".bmp") ||
                name.endsWith(".dds")

            if (!valid)
                return@forEach

            val relative = file.relativeTo(folder).path
            val target = tempDir.resolve(relative)

            target.parentFile.mkdirs()

            file.inputStream().use { input ->
                target.outputStream().use(input::copyTo)
            }

            target.deleteOnExit()
        }
    }

    fun loadDefaultShader(): Shader {
        return loadShader("/shaders/shd_unlit.glsl")
    }

    fun loadShader(path: String): Shader {
        return shaders.getOrPut(path) {
            Shader(loadText(path))
        }
    }

    fun loadModel(path: String): Model {
        return models.getOrPut(path) {
            Model(resolveModelResourcePath(path))
        }
    }

    fun loadDefaultModel(): Model {
        return loadModel("/models/model_standford_bunny.obj")
    }

    fun loadWhiteTexture(): Texture {
        return textures.getOrPut("__white_texture__") {
            Texture.createSolid(Color.white)
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
            setColor("_Color", Color.white)
        }
    }

    fun loadDefaultTextMaterial(): Material {
        return Material(loadShader("/shaders/shd_font.glsl")).apply {
            setColor("_Color", Color.white)
        }
    }

    fun loadDefaultFont(): Font {
        return loadFont(
            texturePath = "/fonts/font_tex_pixelated.png",
            structurePath = "/fonts/font_structure_pixelated.json"
        )
    }

    fun loadFont(
        texturePath: String,
        structurePath: String
    ): Font {
        return Font.loadFont(
            loadTexture(texturePath),
            loadText(structurePath)
        )
    }

    fun loadCubeMap(paths: Array<String>): CubeMap {
        val resolvedPaths = paths.map { path ->
            if (File(path).isAbsolute) path else resolveTexturePath(path)
        }.toTypedArray()

        val key = resolvedPaths.joinToString("|")
        return cubeMaps.getOrPut(key) {
            CubeMap(resolvedPaths)
        }
    }

    override fun close() {
        shaders.values.forEach(Shader::close)
        textures.values.forEach(Texture::close)
        audioClips.values.forEach(AudioClip::close)
        models.values.forEach(Model::close)
        cubeMaps.values.forEach(CubeMap::close)

        shaders.clear()
        textures.clear()
        audioClips.clear()
        models.clear()
        modelsPaths.clear()
        cubeMaps.clear()

    }
}