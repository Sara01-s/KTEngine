package engine.game

import engine.renderer.Window
import glm_.vec2.Vec2
import org.lwjgl.glfw.GLFW.*

enum class Player { P1, P2 }

private data class KeyMap(val up: Int, val down: Int, val left: Int, val right: Int)

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

    private const val ACCELERATION = 5.0f
    private const val FRICTION     = 100.0f

    fun update(window: Window, delta: Float) {
        for (player in Player.entries) {
            val playerNumber = player.ordinal
            val a = axes[playerNumber]
            val k = keys[player] ?: continue

            a.x = axis(current = a.x, window = window, delta = delta, left = k.left, right = k.right)
            a.y = axis(current = a.y, window = window, delta = delta, up = k.up, down = k.down)
        }
    }

    fun getAxis(player: Player): Vec2 = axes[player.ordinal]

    private fun axis(
        current: Float,
        window: Window,
        delta: Float,
        left: Int = 0,
        right: Int = 0,
        up: Int = 0,
        down: Int = 0,
    ): Float {
        var target = 0f

        if (glfwGetKey(window.handle, up)    == GLFW_PRESS) target += 1f
        if (glfwGetKey(window.handle, down)  == GLFW_PRESS) target -= 1f
        if (glfwGetKey(window.handle, right) == GLFW_PRESS) target += 1f
        if (glfwGetKey(window.handle, left)  == GLFW_PRESS) target -= 1f

        target = target.coerceIn(-1f, 1f)

        val rate = if (target != 0f) ACCELERATION else FRICTION

        return moveToward(current, target, rate * delta)
    }

    private fun moveToward(current: Float, target: Float, step: Float): Float {
        val delta = target - current

        return if (kotlin.math.abs(delta) <= step) target
        else current + kotlin.math.sign(delta) * step
    }
}