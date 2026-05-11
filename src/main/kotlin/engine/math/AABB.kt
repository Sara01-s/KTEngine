package engine.math

import glm_.vec2.Vec2

data class AABB(var center: Vec2 = Vec2(), var extent: Vec2 = Vec2(0.5f, 0.5f)) {
    val min get() = Vec2(center.x - extent.x, center.y - extent.y)
    val max get() = Vec2(center.x + extent.x, center.y + extent.y)

    private val previous = mutableSetOf<AABB>()
    private val current  = mutableSetOf<AABB>()

    var onEnter: ((AABB) -> Unit)? = null
    var onStay:  ((AABB) -> Unit)? = null
    var onExit:  ((AABB) -> Unit)? = null

    fun intersects(other: AABB): Boolean {
        return min.x < other.max.x &&
                max.x > other.min.x &&
                min.y < other.max.y &&
                max.y > other.min.y
    }

    fun update(others: List<AABB>) {
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