package engine.systems

import engine.scenes.EmptyScene
import engine.scenes.Scene
import engine.utils.ReactiveCommand

object SceneSystem : AutoCloseable {
    val onSceneLoaded = ReactiveCommand<Scene>()

    var currentScene: Scene = EmptyScene()
        private set

    private var pendingScene: Scene? = null

    fun loadEmptyScene() {
        loadScene(currentScene)
    }

    fun loadScene(scene: Scene) {
        pendingScene = scene
    }

    fun loadPendingScene() {
        val pending = pendingScene ?: return

        currentScene.close()
        RenderSystem.clear()
        CollisionSystem.clear()
        CameraSystem.clear()
        BehaviourSystem.clear()
        Input.clear()

        currentScene = pending
        pendingScene = null

        currentScene.create()
        onSceneLoaded.execute(currentScene)
    }

    override fun close() {
        currentScene.close()
    }
}