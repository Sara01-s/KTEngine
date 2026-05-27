package engine.components

import engine.game.Entity
import engine.game.Time
import engine.systems.Input
import engine.systems.Key
import engine.systems.Player
import engine.systems.Axis
import engine.utils.clamp
import engine.utils.down
import engine.utils.fromEulerAngles
import engine.utils.up
import engine.utils.zero
import glm_.quat.Quat
import glm_.vec3.Vec3

class FirstPersonController : Component {
    override lateinit var entity: Entity

    var moveSpeed = 12f
    var lookSensitivity = 0.15f

    private var pitch = 0f
    private var yaw = 0f

    fun update() {
        val dt = Time.deltaTime

        if (Input.Keyboard.isJustPressed(Key.Escape)) Input.Mouse.captured = false
        if (Input.Keyboard.isJustPressed(Key.Enter))  Input.Mouse.captured = true

        if (Input.Mouse.captured) {
            val mouseDelta = Input.Mouse.delta
            yaw += mouseDelta.x * lookSensitivity
            pitch += mouseDelta.y * lookSensitivity
            pitch = clamp(pitch, -89f, 89f)

            entity.transform.localRotation = Quat.fromEulerAngles(pitch, yaw, 0f)
        }

        val moveDirection = Vec3.zero
        val horizontalAxis = Input.getAxis(Player.P1, Axis.Horizontal)
        val verticalAxis = Input.getAxis(Player.P1, Axis.Vertical)

        if (horizontalAxis != 0f) moveDirection.plusAssign(entity.transform.right * horizontalAxis)
        if (verticalAxis != 0f)   moveDirection.plusAssign(entity.transform.forward * verticalAxis)

        if (Input.Keyboard.isPressed(Key.Space))     moveDirection.plusAssign(Vec3.up)
        if (Input.Keyboard.isPressed(Key.LeftShift)) moveDirection.plusAssign(Vec3.down)

        if (moveDirection.length2() > 0f) {
            moveDirection.normalizeAssign()
        }

        entity.transform.localPosition = entity.transform.localPosition + (moveDirection * moveSpeed * dt)
    }

}