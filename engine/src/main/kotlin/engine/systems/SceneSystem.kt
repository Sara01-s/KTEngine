package engine.systems

import engine.scenes.EmptyScene
import engine.scenes.Scene

object SceneSystem : AutoCloseable {
    val onSceneLoaded: ((scene: Scene) -> Unit) = {}

    var currentScene: Scene = EmptyScene()
        private set

    private var nextScene: Scene? = null

    init {
        loadScene(currentScene)
    }

    fun loadScene(scene: Scene) {
        nextScene = scene
    }

    fun applyPendingScene() {
        val pending = nextScene ?: return

        currentScene.close()
        RenderSystem.clear()
        CameraSystem.clear()
        CollisionSystem.clear()
        BehaviourSystem.clear()
        Input.clear()


        currentScene = pending
        nextScene = null

        onSceneLoaded.invoke(currentScene)
        currentScene.create()
    }

    override fun close() {
        currentScene.close()
    }
}