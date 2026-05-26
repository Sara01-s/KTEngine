package engine.utils

import glm_.glm.dot
import glm_.mat4x4.Mat4
import glm_.quat.Quat
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

fun Vec2.normalized(): Vec2 {
    val mag = sqrt((x * x + y * y).toDouble()).toFloat()
    return if (mag > 0f) Vec2(x / mag, y / mag) else Vec2(0f, 0f)
}

fun Vec3.normalized(): Vec3 {
    val mag = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

    return if (mag > 0f)
        Vec3(x / mag, y / mag, z / mag)
    else
        Vec3(0f, 0f, 0f)
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

fun Mat4.makeFrustum(fovYDegrees: Float, aspectRatio: Float, near: Float, far: Float): Mat4 {
    this.identity()

    val deg2rad = (acos(-1.0) / 180.0).toFloat()

    val tangent = tan((fovYDegrees / 2f * deg2rad).toDouble()).toFloat()
    val top = far * tangent
    val right = top * aspectRatio

    this[0, 0] = far / right
    this[1, 1] = far / top
    this[2, 2] = -(near + far) / (near - far)
    this[2, 3] = -1f
    this[3, 2] = -(2f * near * far) / (near - far)
    this[3, 3] = 0f

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

fun rotationRollPitchYaw(pitch: Float, yaw: Float, roll: Float): Mat4 {
    val mat4 = Mat4().identity()

    val cr = cos(roll.toDouble()).toFloat()
    val sr = sin(roll.toDouble()).toFloat()

    val cp = cos(pitch.toDouble()).toFloat()
    val sp = sin(pitch.toDouble()).toFloat()

    val cy = cos(yaw.toDouble()).toFloat()
    val sy = sin(yaw.toDouble()).toFloat()

    // First Column.
    mat4[0, 0] = cy * cr + sy * sp * sr
    mat4[0, 1] = cp * sr
    mat4[0, 2] = -sy * cr + cy * sp * sr
    mat4[0, 3] = 0f

    // Second Column.
    mat4[1, 0] = -cy * sr + sy * sp * cr
    mat4[1, 1] = cp * cr
    mat4[1, 2] = sy * sr + cy * sp * cr
    mat4[1, 3] = 0f

    // Third Column.
    mat4[2, 0] = sy * cp
    mat4[2, 1] = -sp
    mat4[2, 2] = cy * cp
    mat4[2, 3] = 0f

    // Translation.
    mat4[3, 0] = 0f
    mat4[3, 1] = 0f
    mat4[3, 2] = 0f
    mat4[3, 3] = 1f

    return mat4
}

fun Vec3.transformNormal(matrix: Mat4): Vec3 {
    return Vec3(
        this.x * matrix[0, 0] + this.y * matrix[1, 0] + this.z * matrix[2, 0],
        this.x * matrix[0, 1] + this.y * matrix[1, 1] + this.z * matrix[2, 1],
        this.x * matrix[0, 2] + this.y * matrix[1, 2] + this.z * matrix[2, 2]
    )
}

fun lookAtLH(eye: Vec3, focus: Vec3, up: Vec3): Mat4 {
    val mat4 = Mat4().identity()

    val f = Vec3(focus.x - eye.x, focus.y - eye.y, focus.z - eye.z).normalized()

    // R = Up x Forward
    val r = Vec3(
        up.y * f.z - up.z * f.y,
        up.z * f.x - up.x * f.z,
        up.x * f.y - up.y * f.x
    ).normalized()

    // U = Forward x Right
    val u = Vec3(
        f.y * r.z - f.z * r.y,
        f.z * r.x - f.x * r.z,
        f.x * r.y - f.y * r.x
    )

    mat4[0, 0] = r.x; mat4[1, 0] = r.y; mat4[2, 0] = r.z; mat4[3, 0] = -dot(r, eye)
    mat4[0, 1] = u.x; mat4[1, 1] = u.y; mat4[2, 1] = u.z; mat4[3, 1] = -dot(u, eye)
    mat4[0, 2] = f.x; mat4[1, 2] = f.y; mat4[2, 2] = f.z; mat4[3, 2] = -dot(f, eye)
    mat4[0, 3] = 0f;  mat4[1, 3] = 0f;  mat4[2, 3] = 0f;  mat4[3, 3] = 1f

    return mat4
}

fun scalarModAngle(angle: Float): Float {
    var value = angle % (2f * PI.toFloat())

    if (value > PI.toFloat()) {
        value -= 2f * PI.toFloat()
    }
    else if (value < -PI.toFloat()) {
        value += 2f * PI.toFloat()
    }

    return value
}

fun clamp(value: Float, min: Float, max: Float): Float {
    return if (value < min) {
        min
    }
    else if (value > max) {
        max
    }
    else {
        value
    }
}

val vecUp      = Vec3(0f, 1f, 0f)
val vecRight   = Vec3(1f, 0f, 0f)
val vecForward = Vec3(0f, 0f, 1f)

val Vec3.Companion.up: Vec3 get() = vecUp
val Vec3.Companion.down: Vec3 get() = -vecUp

val Vec3.Companion.right: Vec3 get() = vecRight
val Vec3.Companion.left: Vec3 get() = -vecRight

val Vec3.Companion.forward: Vec3 get() = vecForward
val Vec3.Companion.back: Vec3 get() = -vecForward

val Vec3.Companion.zero: Vec3 get() = Vec3(0f, 0f, 0f)
val Vec3.Companion.one: Vec3 get() = Vec3(1f, 1f, 1f)

fun Quat.Companion.fromEulerAngles(xDegrees: Float, yDegrees: Float, zDegrees: Float): Quat {
    val factor = 0.017453292f * 0.5f

    val pitchRad = xDegrees * factor
    val yawRad   = yDegrees * factor
    val rollRad  = zDegrees * factor

    val cx = cos(pitchRad)
    val sx = sin(pitchRad)
    val cy = cos(yawRad)
    val sy = sin(yawRad)
    val cz = cos(rollRad)
    val sz = sin(rollRad)

    val qw = cx * cy * cz + sx * sy * sz
    val qx = sx * cy * cz + cx * sy * sz
    val qy = cx * sy * cz - sx * cy * sz
    val qz = cx * cy * sz - sx * sy * cz

    return Quat(qw, qx, qy, qz)
}

fun Quat.Companion.fromEulerAngles(euler: Vec3): Quat {
    return fromEulerAngles(euler.x, euler.y, euler.z)
}