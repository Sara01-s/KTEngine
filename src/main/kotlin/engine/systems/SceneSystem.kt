package engine.systems

import engine.scenes.Scene
import engine.utils.LogLevel
import engine.utils.log

object SceneSystem : AutoCloseable {

    var currentScene: Scene? = null
        private set
        get() {
            if (field == null) {
                log("No Scene currently loaded", LogLevel.Error)
                return null
            }

            return field
        }

    fun load(scene: Scene) {
        currentScene?.close()
        currentScene = scene
    }

    override fun close() {
        currentScene?.close()
    }
}