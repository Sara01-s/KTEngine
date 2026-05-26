package engine.components

import engine.game.Entity
import engine.utils.one
import engine.utils.zero
import glm_.mat4x4.Mat4
import glm_.quat.Quat
import glm_.vec3.Vec3
import kotlin.math.cos
import kotlin.math.sin

class Transform(
    position: Vec3 = Vec3.zero,
    scale: Vec3 = Vec3.one,
    rotation: Quat = Quat.identity,
) : Component {

    override lateinit var entity: Entity

    // ── Local space ──────────────────────────────────────────────────────────

    var localPosition: Vec3 = position
        set(value) {
            field = value
            markDirty()
        }

    var localScale: Vec3 = scale
        set(value) {
            field = value
            markDirty()
        }

    var localRotation: Quat = rotation
        set(value) {
            field = value
            markDirty()
        }

    // LH, Y-up: forward = +Z, right = +X, up = +Y
    val forward: Vec3 get() = worldRotation * Vec3(0f, 0f, 1f)
    val right:   Vec3 get() = worldRotation * Vec3(1f, 0f, 0f)
    val up:      Vec3 get() = worldRotation * Vec3(0f, 1f, 0f)

    // ── Hierarchy ────────────────────────────────────────────────────────────

    var parent: Transform? = null
        private set

    private val _children = mutableListOf<Transform>()
    val children: List<Transform> get() = _children

    fun addChild(child: Transform) {
        if (child === this || child.isAncestorOf(this)) return
        child.parent?.removeChild(child)

        val worldPos = child.worldPosition
        val worldRot = child.worldRotation
        val worldScl = child.worldScale

        child.parent = this
        _children += child

        child.setWorldTransform(worldPos, worldRot, worldScl)
    }

    fun removeChild(child: Transform) {
        if (!_children.remove(child)) return

        val worldPos = child.worldPosition
        val worldRot = child.worldRotation
        val worldScl = child.worldScale

        child.parent = null

        child.setWorldTransform(worldPos, worldRot, worldScl)
    }

    fun setParent(newParent: Transform?) {
        newParent?.addChild(this) ?: parent?.removeChild(this)
    }

    fun isAncestorOf(other: Transform): Boolean {
        var current = other.parent
        while (current != null) {
            if (current === this) return true
            current = current.parent
        }
        return false
    }

    // ── World space ──────────────────────────────────────────────────────────

    var worldPosition: Vec3
        get() = Vec3(worldMatrix[3])
        set(value) {
            val parentMat = parent?.worldMatrix
            localPosition = if (parentMat != null) {
                Vec3(parentMat.inverse() * glm_.vec4.Vec4(value, 1f))
            } else {
                value
            }
        }

    var worldRotation: Quat
        get() {
            return parent?.worldRotation?.times(localRotation) ?: localRotation
        }
        set(value) {
            localRotation = (parent?.worldRotation?.inverse() ?: Quat.identity) * value
        }

    var worldScale: Vec3
        get() {
            val wm = worldMatrix
            val sx = Vec3(wm[0].x, wm[0].y, wm[0].z).length()
            val sy = Vec3(wm[1].x, wm[1].y, wm[1].z).length()
            val sz = Vec3(wm[2].x, wm[2].y, wm[2].z).length()
            return Vec3(sx, sy, sz)
        }
        set(value) {
            val parentScale = parent?.worldScale ?: Vec3.one
            localScale = Vec3(value.x / parentScale.x, value.y / parentScale.y, value.z / parentScale.z)
        }

    // ── Matrices ─────────────────────────────────────────────────────────────

    private var _localMatrix: Mat4 = Mat4.identity
    private var _worldMatrix: Mat4 = Mat4.identity
    private var dirty = true

    private val isDirty: Boolean
        get() = dirty || (parent?.isDirty ?: false)

    val localMatrix: Mat4
        get() {
            if (dirty) rebuildLocalMatrix()
            return _localMatrix
        }

    val worldMatrix: Mat4
        get() {
            if (isDirty) rebuildWorldMatrices()
            return _worldMatrix
        }

    private fun markDirty() {
        if (!dirty) {
            dirty = true
            _children.forEach { it.markDirty() }
        }
    }

    private fun rebuildLocalMatrix() {
        _localMatrix = Mat4.identity
            .translate(localPosition)
            .times(localRotation.toMat4())
            .scale(localScale)
        dirty = false
    }

    private fun rebuildWorldMatrices() {
        if (dirty) {
            rebuildLocalMatrix()
        }

        _worldMatrix = parent?.worldMatrix?.times(_localMatrix) ?: _localMatrix
    }

    // ── Mutation helpers ─────────────────────────────────────────────────────

    fun translate(delta: Vec3) {
        localPosition = localPosition + delta
    }

    fun translateXZ(delta: Vec3) {
        localPosition = localPosition + Vec3(delta.x, 0f, delta.z)
    }

    fun rotate(pitch: Float = 0f, yaw: Float = 0f, roll: Float = 0f) {
        val qPitch = Quat(cos(pitch / 2f), sin(pitch / 2f), 0f, 0f)
        val qYaw   = Quat(cos(yaw   / 2f), 0f, sin(yaw   / 2f), 0f)
        val qRoll  = Quat(cos(roll  / 2f), 0f, 0f, sin(roll  / 2f))

        localRotation = localRotation * qYaw * qPitch * qRoll
    }

    fun rotate(delta: Vec3) {
        rotate(delta.x, delta.y, delta.z)
    }

    private fun setWorldTransform(pos: Vec3, rot: Quat, scl: Vec3) {
        worldPosition = pos
        worldRotation = rot
        worldScale    = scl
    }
}