package engine

import engine.utils.log
import engine.utils.logError
import engine.utils.logWarn
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

object Application {

    val rootPath: Path by lazy {
        Path(System.getProperty("user.dir"))
            .toAbsolutePath()
            .normalize()
    }

    val assetsPath: Path by lazy {
        rootPath
            .resolve("assets")
            .toAbsolutePath()
            .normalize()
    }

    val persistentDataPath: Path by lazy {
        rootPath
            .resolve("persistentData")
            .toAbsolutePath()
            .normalize()
            .also(Files::createDirectories)
    }

    val tempPath: Path by lazy {
        rootPath
            .resolve(".temp")
            .toAbsolutePath()
            .normalize()
            .also(Files::createDirectories)
    }

    init {
        log("Application root path: $rootPath")

        if (!Files.exists(assetsPath)) {
            logError("Assets folder not found: $assetsPath")
            logWarn("Create an 'assets' folder in the project root.")
        }
        else {
            log("Assets folder found: $assetsPath")
        }

        if (!Files.exists(persistentDataPath)) {
            logWarn("Persistent data folder missing, creating: $persistentDataPath")
            Files.createDirectories(persistentDataPath)
        }

        if (!Files.exists(tempPath)) {
            logWarn("Temp folder missing, creating: $tempPath")
            Files.createDirectories(tempPath)
        }
    }
}