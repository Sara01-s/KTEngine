package engine.systems

import engine.components.Camera

object CameraSystem : AutoCloseable {
    private val cameras = mutableListOf<Camera>()

    var main: Camera? = null
        private set

    fun register(camera: Camera) {
        cameras.add(camera)
        
        if (main == null) {
            main = camera
        }
    }

    fun unregister(camera: Camera) {
        cameras.remove(camera)
        if (main === camera) {
            main = cameras.firstOrNull()
        }
    }

    fun setMain(camera: Camera) {
        require(cameras.contains(camera)) { "Camera must be registered before setting as main" }
        main = camera
    }

    fun getAllCameras(): List<Camera> {
        return cameras.toList()
    }

    fun clear() {
        cameras.clear()
        main = null
    }

    override fun close() {
        clear()
    }
}