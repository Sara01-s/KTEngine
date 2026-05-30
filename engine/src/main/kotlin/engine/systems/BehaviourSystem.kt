package engine.systems

import engine.components.Behaviour

object BehaviourSystem {

    private val behaviours      = mutableListOf<Behaviour>()
    private val pendingAdd      = mutableListOf<Behaviour>()
    private val pendingRemove   = mutableListOf<Behaviour>()
    private var isIterating     = false

    fun register(behaviour: Behaviour) {
        if (isIterating) {
            pendingAdd.add(behaviour)
        } else {
            behaviours.add(behaviour)
        }
    }

    fun unregister(behaviour: Behaviour) {
        if (isIterating) {
            pendingRemove.add(behaviour)
        } else {
            behaviours.remove(behaviour)
        }
    }

    fun update() {
        flushPending()
        isIterating = true

        for (behaviour in behaviours) {
            if (behaviour.enabled) {
                behaviour.update()
            }
        }

        isIterating = false
        flushPending()
    }

    fun fixedUpdate() {
        flushPending()
        isIterating = true

        for (behaviour in behaviours) {
            if (behaviour.enabled) {
                behaviour.fixedUpdate()
            }
        }

        isIterating = false
        flushPending()
    }

    fun draw() {
        flushPending()
        isIterating = true

        for (behaviour in behaviours) {
            if (behaviour.enabled) {
                behaviour.onDraw()
            }
        }

        isIterating = false
        flushPending()
    }

    fun clear() {
        behaviours.clear()
        pendingAdd.clear()
        pendingRemove.clear()
    }

    private fun flushPending() {
        if (pendingAdd.isNotEmpty()) {
            behaviours.addAll(pendingAdd)
            pendingAdd.clear()
        }
        if (pendingRemove.isNotEmpty()) {
            behaviours.removeAll(pendingRemove.toSet())
            pendingRemove.clear()
        }
    }
}