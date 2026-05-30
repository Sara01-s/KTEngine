package engine.systems

import engine.components.behaviours.DirectionalLight

object LightingSystem {
    var directionalLight: DirectionalLight? = null

    fun register(directionalLight: DirectionalLight) {
        println("[LightingSystem] Luz registrada: ${directionalLight.entity.name}")
        this.directionalLight = directionalLight
    }

    fun unregister() {
        this.directionalLight = null
    }

    fun isEnabled(): Boolean {
        return directionalLight != null
    }
}