package engine.systems

import engine.scenes.Scene

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
        BehaviourSystem.clear()
        Input.clear()

        currentScene = pending
        nextScene = null

        currentScene?.start()
    }

    override fun close() {
        currentScene?.close()
    }
}