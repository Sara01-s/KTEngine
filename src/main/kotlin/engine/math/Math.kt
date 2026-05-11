package engine.math

import glm_.mat4x4.Mat4
import glm_.vec2.Vec2

fun Vec2.normalized(): Vec2 {
    val mag = kotlin.math.sqrt((x * x + y * y).toDouble()).toFloat()
    return if (mag > 0f) Vec2(x / mag, y / mag) else Vec2(0f, 0f)
}

fun Mat4.ortho(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Mat4 {
    this.identity()

    this[0, 0] = 2f / (right - left)
    this[1, 1] = 2f / (top - bottom)
    this[2, 2] = -2f / (far - near)

    this[3, 0] = -(right + left) / (right - left)
    this[3, 1] = -(top + bottom) / (top - bottom)
    this[3, 2] = -(far + near) / (far - near)

    return this
}

fun Mat4.translate(x: Float, y: Float, z: Float = 0f): Mat4 {
    this[3, 0] += this[0, 0] * x + this[1, 0] * y + this[2, 0] * z
    this[3, 1] += this[0, 1] * x + this[1, 1] * y + this[2, 1] * z
    this[3, 2] += this[0, 2] * x + this[1, 2] * y + this[2, 2] * z
    return this
}

fun Mat4.scale(x: Float, y: Float, z: Float = 1f): Mat4 {
    this[0, 0] *= x; this[0, 1] *= x; this[0, 2] *= x
    this[1, 0] *= y; this[1, 1] *= y; this[1, 2] *= y
    this[2, 0] *= z; this[2, 1] *= z; this[2, 2] *= z
    return this
}