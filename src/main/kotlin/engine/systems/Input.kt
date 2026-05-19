package engine.game

import engine.rendering.Window
import glm_.vec2.Vec2
import org.lwjgl.glfw.GLFW.*
import kotlin.math.abs
import kotlin.math.sign

enum class Player { P1, P2 }

private data class KeyMap(
    val up: Int,
    val down: Int,
    val left: Int,
    val right: Int
)

private val keys = mapOf(
    Player.P1 to KeyMap(
        up = GLFW_KEY_W,
        down = GLFW_KEY_S,
        left = GLFW_KEY_A,
        right = GLFW_KEY_D
    ),
    Player.P2 to KeyMap(
        up = GLFW_KEY_UP,
        down = GLFW_KEY_DOWN,
        left = GLFW_KEY_LEFT,
        right = GLFW_KEY_RIGHT
    )
)

object Input {

    private val axes = arrayOf(Vec2(), Vec2())

    private val currentKeys = BooleanArray(GLFW_KEY_LAST + 1)
    private val previousKeys = BooleanArray(GLFW_KEY_LAST + 1)

    private lateinit var window: Window

    private const val ACCELERATION = 5.0f
    private const val FRICTION = 100.0f

    fun update(window: Window, delta: Float) {

        this.window = window

        // Save previous frame.
        for (i in currentKeys.indices) {
            previousKeys[i] = currentKeys[i]
        }

        // Poll current frame.
        for (i in currentKeys.indices) {
            currentKeys[i] = glfwGetKey(window.handle, i) == GLFW_PRESS
        }

        // Update axes.
        for (player in Player.entries) {
            val playerNumber = player.ordinal
            val a = axes[playerNumber]
            val k = keys[player] ?: continue

            a.x = axis(
                current = a.x,
                delta = delta,
                left = k.left,
                right = k.right
            )

            a.y = axis(
                current = a.y,
                delta = delta,
                up = k.up,
                down = k.down
            )
        }
    }

    fun getAxis(player: Player): Vec2 {
        return axes[player.ordinal]
    }

    fun isKeyPressed(key: Int): Boolean {
        return currentKeys[key]
    }

    fun isKeyJustPressed(key: Int): Boolean {
        return currentKeys[key] && !previousKeys[key]
    }

    fun isKeyJustReleased(key: Int): Boolean {
        return !currentKeys[key] && previousKeys[key]
    }

    private fun axis(
        current: Float,
        delta: Float,
        left: Int = 0,
        right: Int = 0,
        up: Int = 0,
        down: Int = 0,
    ): Float {

        var target = 0f

        if (isKeyPressed(up)) target += 1f
        if (isKeyPressed(down)) target -= 1f
        if (isKeyPressed(right)) target += 1f
        if (isKeyPressed(left)) target -= 1f

        target = target.coerceIn(-1f, 1f)

        val rate =
            if (target != 0f) ACCELERATION
            else FRICTION

        return moveToward(current, target, rate * delta)
    }

    private fun moveToward(
        current: Float,
        target: Float,
        step: Float
    ): Float {

        val delta = target - current

        return if (abs(delta) <= step)
            target
        else
            current + sign(delta) * step
    }
}