package engine.game

import engine.utils.LogLevel
import engine.utils.log

object SceneManager : AutoCloseable {

    var currentScene: Scene? = null
        private set
        get() {
            if (field != null) {
                return field
            }

            log("No Scene currently loaded", LogLevel.Error)
            return null
        }

    fun load(scene: Scene) {
        currentScene?.close()
        currentScene = scene
    }

    override fun close() {
        currentScene?.close()
    }
}