package engine.assets

import engine.utils.logError
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Files.copy
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.absolutePathString

object AssetUtils {
    fun normalize(path: String): String {
        return path
            .replace('\\', '/')
            .removePrefix("/engine_assets/")
            .removePrefix("engine_assets/")
            .trimStart('/')
    }

    fun requireExists(path: Path) {
        if (!Files.exists(path)) {
            logError("Asset not found: $path")
            error("Asset not found: $path")
        }
    }

    fun absolutePath(path: Path): String {
        requireExists(path)
        return path.absolutePathString()
    }

    fun loadText(path: Path): String {
        requireExists(path)
        return Files.readString(path)
    }

    fun loadBytes(path: Path): ByteArray {
        requireExists(path)
        return Files.readAllBytes(path)
    }

    fun loadStream(path: Path): InputStream {
        requireExists(path)
        return Files.newInputStream(path)
    }

    fun loadResourceText(owner: Any, path: String): String {
        val resource = owner.javaClass.getResource(path)

        return resource?.readText() ?: run {
            logError("Engine asset not found: $path")
            error("Engine asset not found: $path")
        }
    }

    fun loadResourceBytes(owner: Any, path: String): ByteArray {
        return loadResourceStream(owner, path).readBytes()
    }

    fun loadResourceStream(owner: Any, path: String): InputStream {
        return owner.javaClass.getResourceAsStream(path) ?: run {
            logError("Engine asset not found: $path")
            error("Engine asset not found: $path")
        }
    }

    fun createTempFileFromResource(context: Any, resourcePath: String): String {
        val tempFile = Files.createTempFile("engine_asset_", ".tmp")
        val stream = context.javaClass.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")

        stream.use { input ->
            copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING)
        }

        tempFile.toFile().deleteOnExit()
        return tempFile.toAbsolutePath().toString()
    }
}