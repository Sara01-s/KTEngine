package engine.systems

import engine.Application
import engine.rendering.bindables.Shader
import engine.utils.log
import engine.utils.logError
import java.io.File

object ShaderSystem {
    private data class ShaderEntry(
        val shader: Shader,
        val mainPath: String,
        val dependencies: MutableList<String> = mutableListOf(),
        var lastCheckTime: Long = 0L
    )

    private val activeShaders = mutableListOf<ShaderEntry>()

    fun register(shader: Shader, path: String) {
        val entry = ShaderEntry(shader, path)

        refreshDependencies(entry)
        activeShaders.add(entry)
    }

    private fun refreshDependencies(entry: ShaderEntry) {
        entry.dependencies.clear()
        entry.dependencies.add(entry.mainPath)
        entry.dependencies.addAll(entry.shader.getDependencies())
    }

    fun update() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastGlobalCheck < 500) {
            return
        }

        lastGlobalCheck = currentTime

        for (entry in activeShaders) {
            val hasChanged = entry.dependencies.any { depPath ->
                val file = File(Application.assetsPath.resolve(depPath).toString())
                file.exists() && file.lastModified() > entry.lastCheckTime
            }

            if (hasChanged) {
                log("[ShaderSystem] Change detected in dependencies of: ${entry.mainPath}")

                try {
                    entry.shader.compile()
                    entry.lastCheckTime = currentTime
                    refreshDependencies(entry)
                } catch (e: Exception) {
                    logError("[ShaderSystem] Error reloading: ${e.message}")
                }
            }
        }
    }
    private var lastGlobalCheck = 0L
}