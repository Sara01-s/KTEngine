package engine.systems

import engine.components.behaviours.DirectionalLight
import engine.utils.log

data object LightingSystem {
    var directionalLight: DirectionalLight? = null
        private set

    fun register(directionalLight: DirectionalLight) {
        log("[LightingSystem] Light registered: ${directionalLight.entity.name}")
        this.directionalLight = directionalLight
    }

    fun unregister() {
        this.directionalLight = null
    }

    fun isEnabled(): Boolean {
        return directionalLight != null
    }
}