package engine.components.behaviours

import engine.components.Behaviour
import engine.game.Time
import engine.systems.LightingSystem
import engine.utils.Color
import engine.utils.up
import glm_.quat.Quat
import glm_.vec3.Vec3

class DirectionalLight : Behaviour() {
    var intensity = 1.0f
    var color = Color.white

    override fun onEnable() {
        LightingSystem.register(this)
    }

    override fun onDisable() {
        LightingSystem.unregister()
    }

    override fun update() {
        transform.localRotation = Quat().angleAxis(Time.time, Vec3.up)
    }
}