package engine.systems

import engine.components.Collider2D

object CollisionSystem {

    private val colliders = mutableListOf<Collider2D>()

    fun register(collider: Collider2D) {
        colliders.add(collider)
    }

    fun unregister(collider: Collider2D) {
        colliders.remove(collider)
    }

    fun update() {
        for (collider in colliders) {
            collider.updateCollisions(colliders)
        }
    }

    fun clear() {
        colliders.forEach { unregister(it) }
        colliders.clear()
    }
}