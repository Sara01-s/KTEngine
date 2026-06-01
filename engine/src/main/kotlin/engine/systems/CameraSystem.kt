package engine.systems

import engine.components.Camera
import engine.utils.Tags

data object CameraSystem : AutoCloseable {
    private var _main: Camera? = null
    private var _sceneCamera: Camera? = null

    val main: Camera?
        get() {
            if (_main == null) {
                val scene = SceneSystem.currentScene

                _main = scene.entities
                    .firstOrNull { it.compareTag(Tags.MAIN_CAMERA) }
                    ?.getComponent<Camera>()
            }

            return _main
        }

    val sceneCamera: Camera?
        get() {
            if (_sceneCamera == null) {
                val scene = SceneSystem.currentScene

                _sceneCamera = scene.entities
                    .firstOrNull { it.compareTag(Tags.SCENE_CAMERA) }
                    ?.getComponent<Camera>()
            }

            return _sceneCamera
        }

    fun clear() {
        _main = null
    }

    override fun close() {
        clear()
    }
}