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
        RenderSystem.clear()
        CameraSystem.clear()
        CollisionSystem.clear()
        Input.clear()

        currentScene = pending
        nextScene = null

        currentScene?.start()
    }

    override fun close() {
        currentScene?.close()
    }
}