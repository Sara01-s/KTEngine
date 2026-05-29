package engine.components

import engine.systems.CollisionSystem
import glm_.vec2.Vec2
import kotlin.math.abs

class Collider2D(
    var center: Vec2 = Vec2(0f),
    var extent: Vec2 = Vec2(0.5f)
) : Component() {

    private val previous = mutableSetOf<Collider2D>()
    private val current  = mutableSetOf<Collider2D>()

    var onEnter: ((Collider2D) -> Unit)? = null
    var onStay:  ((Collider2D) -> Unit)? = null
    var onExit:  ((Collider2D) -> Unit)? = null

    override fun onAdded() {
        CollisionSystem.register(this)
    }

    override fun onRemoved() {
        CollisionSystem.unregister(this)
    }

    fun intersects(other: Collider2D): Boolean {
        val worldCenter = Vec2(entity.transform.worldPosition.x, entity.transform.worldPosition.y) + center
        val worldOtherCenter = Vec2(other.entity.transform.worldPosition.x, other.entity.transform.worldPosition.y) + other.center

        val totalExtent = extent + other.extent
        return abs(worldCenter.x - worldOtherCenter.x) < totalExtent.x &&
                abs(worldCenter.y - worldOtherCenter.y) < totalExtent.y
    }

    fun updateCollisions(others: List<Collider2D>) {
        current.clear()

        for (other in others) {
            if (other === this) {
                continue
            }

            if (intersects(other)) {
                current.add(other)
            }
        }

        for (other in current) {
            if (other !in previous) {
                onEnter?.invoke(other)
            }
            else {
                onStay?.invoke(other)
            }
        }

        for (other in previous) {
            if (other !in current) {
                onExit?.invoke(other)
            }
        }

        previous.clear()
        previous.addAll(current)
    }
}