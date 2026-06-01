package engine.components.behaviours

import engine.components.Behaviour
import engine.game.Time
import engine.systems.Axis
import engine.systems.Input
import engine.systems.Key
import engine.systems.Player
import engine.utils.clamp
import engine.utils.degrees
import engine.utils.down
import engine.utils.normalized
import engine.utils.up
import engine.utils.zero
import glm_.quat.Quat
import glm_.vec3.Vec3
import kotlin.math.PI

class FirstPersonController : Behaviour() {
    var moveSpeed = 8f
    var lookSensitivity = 0.15f

    private var pitch = 0f
    private var yaw = 0f

    override fun start() {
        Input.Mouse.captured = true

        val fwd = transform.forward
        yaw   = degrees(kotlin.math.atan2(fwd.x, fwd.z))
        pitch = degrees(kotlin.math.asin(-fwd.y))
    }

    override fun update() {
        if (Input.Keyboard.isJustPressed(Key.Escape)) {
            Input.Mouse.captured = !Input.Mouse.captured
        }

        if (!Input.Mouse.captured) {
            return
        }

        // Mouse rotation
        val mouseDelta = Input.Mouse.delta
        yaw   += mouseDelta.x * lookSensitivity
        pitch += mouseDelta.y * lookSensitivity
        pitch  = clamp(pitch, -89.9f, 89.9f)

        val qYaw = Quat().angleAxis(yaw * (PI.toFloat() / 180f), Vec3(0f, 1f, 0f))
        val qPitch = Quat().angleAxis(pitch * (PI.toFloat() / 180f), Vec3(1f, 0f, 0f))

        transform.localRotation = qYaw * qPitch

        // Movement
        val moveDirection = Vec3.zero
        val horizontalAxis = Input.getAxis(Player.P1, Axis.Horizontal)
        val verticalAxis   = Input.getAxis(Player.P1, Axis.Vertical)

        val fwdFlat = transform.forward
        val forwardFlat = Vec3(fwdFlat.x, 0f, fwdFlat.z).normalize()
        val rightFlat = Vec3.up.cross(forwardFlat).normalize()

        if (horizontalAxis != 0f) moveDirection += rightFlat * horizontalAxis
        if (verticalAxis   != 0f) moveDirection += forwardFlat * verticalAxis

        if (Input.Keyboard.isPressed(Key.Space))     moveDirection += Vec3.up
        if (Input.Keyboard.isPressed(Key.LeftShift)) moveDirection += Vec3.down

        if (moveDirection.length2() > 0.0001f) {
            val velocity = moveDirection.normalized() * moveSpeed * Time.deltaTime
            transform.worldPosition = transform.worldPosition.plus(velocity)
        }
    }
}