package engine.components.behaviours

import engine.components.Behaviour
import engine.systems.LightingSystem
import engine.utils.Color

class DirectionalLight : Behaviour() {
    var intensity = 1.0f
    var color = Color.white

    override fun onEnable() {
        LightingSystem.register(this)
    }

    override fun onDisable() {
        LightingSystem.unregister()
    }
}