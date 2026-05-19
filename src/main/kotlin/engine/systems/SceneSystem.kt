package engine.systems

import engine.scenes.Scene
import engine.utils.LogLevel
import engine.utils.log

object SceneSystem : AutoCloseable {

    var currentScene: Scene? = null
        private set

    private var nextScene: Scene? = null

    fun loadScene(scene: Scene) {
        nextScene = scene
    }

    fun applyPendingScene() {
        val pending = nextScene ?: return

        currentScene?.close()
        currentScene = pending
        nextScene = null
    }

    override fun close() {
        currentScene?.close()
    }
}