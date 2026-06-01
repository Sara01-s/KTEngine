package org.sara01.behaviours

import engine.components.Behaviour
import engine.game.Time
import engine.systems.Axis
import engine.systems.Input
import engine.systems.Key
import engine.systems.Player
import engine.utils.clamp
import engine.utils.down
import engine.utils.eulerAnglesDeg
import engine.utils.up
import engine.utils.zero
import glm_.quat.Quat
import glm_.vec3.Vec3

class FirstPersonController : Behaviour() {
    var moveSpeed = 12f
    var lookSensitivity = 0.15f

    private var pitch = 0f
    private var yaw = 0f

    override fun start() {
        Input.Mouse.captured = true
    }

    override fun update() {
        if (Input.Keyboard.isJustPressed(Key.Escape)) Input.Mouse.captured = false
        if (Input.Keyboard.isJustPressed(Key.Enter))  Input.Mouse.captured = true

        if (Input.Mouse.captured) {
            val mouseDelta = Input.Mouse.delta
            yaw   += mouseDelta.x * lookSensitivity
            pitch += mouseDelta.y * lookSensitivity
            pitch  = clamp(pitch, -89f, 89f)

            transform.localRotation = Quat.eulerAnglesDeg(pitch, yaw, 0f)
        }

        val moveDirection = Vec3.zero
        val horizontalAxis = Input.getAxis(Player.P1, Axis.Horizontal)
        val verticalAxis   = Input.getAxis(Player.P1, Axis.Vertical)

        if (horizontalAxis != 0f) moveDirection += transform.right   * horizontalAxis
        if (verticalAxis   != 0f) moveDirection += transform.forward * verticalAxis

        if (Input.Keyboard.isPressed(Key.Space))     moveDirection += Vec3.up
        if (Input.Keyboard.isPressed(Key.LeftShift)) moveDirection += Vec3.down

        if (moveDirection.length2() > 0f) moveDirection.normalizeAssign()

        transform.localPosition = transform.localPosition + (moveDirection * moveSpeed * Time.deltaTime)
    }
}