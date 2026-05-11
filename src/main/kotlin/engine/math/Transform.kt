package engine.math

import glm_.vec2.Vec2

class Transform(
    position: Vec2 = Vec2(0f, 0f),
    scale: Vec2 = Vec2(1f, 1f),
    pivot: Vec2 = Vec2(0f, 0f)
) {
    var onChanged: (() -> Unit)? = null

    var position: Vec2 = position
        set(value) {
            field = value
            onChanged?.invoke()
        }

    var scale: Vec2 = scale
        set(value) {
            field = value
            onChanged?.invoke()
        }

    var pivot: Vec2 = pivot
        set(value) {
            field = value
            onChanged?.invoke()
        }
}