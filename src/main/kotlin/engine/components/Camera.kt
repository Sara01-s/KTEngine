package engine.components

import engine.game.Entity
import engine.systems.CameraSystem

class Camera : Component {

    override lateinit var entity: Entity

    var pitch = 0f
    var yaw = 0f
    var roll = 0f

    override fun onAdded() {
        CameraSystem.register(this)
    }

    override fun onRemoved() {
        CameraSystem.unregister(this)
    }
}