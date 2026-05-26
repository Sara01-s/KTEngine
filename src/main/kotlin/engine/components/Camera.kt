package engine.components

import engine.game.Entity
import engine.systems.CameraSystem

class Camera : Component {

    override lateinit var entity: Entity

    var pitch = 0f
        private set

    var yaw = 0f
        private set

    override fun onAdded() {
        CameraSystem.register(this)
    }

    override fun onRemoved() {
        CameraSystem.unregister(this)
    }
}