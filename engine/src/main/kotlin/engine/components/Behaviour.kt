package engine.components

import engine.systems.BehaviourSystem

abstract class Behaviour : Component() {
    val transform get() = entity.transform

    var enabled: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            if (value) onEnable() else onDisable()
        }

    open fun start() {}
    open fun update() {}
    open fun fixedUpdate() {}
    open fun onDestroy() {}
    open fun onEnable() {}
    open fun onDisable() {}

    final override fun onAdded() {
        BehaviourSystem.register(this)
        start()
    }

    final override fun onRemoved() {
        onDestroy()
        BehaviourSystem.unregister(this)
    }

    inline fun <reified T : Component> getComponent(): T =
        entity.getComponent<T>()

    inline fun <reified T : Component> hasComponent(): Boolean =
        entity.hasComponent<T>()
}