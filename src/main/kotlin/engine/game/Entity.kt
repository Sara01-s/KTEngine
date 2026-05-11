package engine.game

import engine.math.AABB
import engine.math.Transform
import engine.renderer.drawables.Drawable
import glm_.vec2.Vec2

class Entity(
    val transform: Transform = Transform(),
    val drawable: Drawable,
    val collider: AABB = AABB()
) : AutoCloseable{

    init {
        transform.onChanged = { sync() }
        sync()
    }

    fun sync() {
        collider.center = transform.position
        collider.extent = Vec2(
            transform.scale.x * 0.5f,
            transform.scale.y * 0.5f
        )

        drawable.transform.position = transform.position
        drawable.transform.scale = transform.scale
    }

    override fun close() {
        drawable.close()
    }
}