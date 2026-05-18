package engine.components

import engine.game.CollisionSystem
import engine.game.Entity
import glm_.vec2.Vec2
import kotlin.math.abs

data class Collider2D(
    var center: Vec2 = Vec2(0f),
    var extent: Vec2 = Vec2(0.5f)
) : Component {

    override lateinit var entity: Entity

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
        val transform = entity.transform
        val center = transform.position
        val extent = Vec2(transform.scale.x * 0.5f, transform.scale.y * 0.5f)

        val otherTransform = other.entity.transform
        val otherCenter = otherTransform.position
        val otherExtent = Vec2(otherTransform.scale.x * 0.5f, otherTransform.scale.y * 0.5f)

        return abs(center.x - otherCenter.x) < (extent.x + otherExtent.x) &&
                abs(center.y - otherCenter.y) < (extent.y + otherExtent.y)
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