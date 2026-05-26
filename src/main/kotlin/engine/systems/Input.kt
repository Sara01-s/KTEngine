package engine.systems

import engine.rendering.Window
import glm_.vec2.Vec2
import org.lwjgl.glfw.GLFW.*
import kotlin.math.abs
import kotlin.math.sign

// ── Key enum ─────────────────────────────────────────────────────────────────

enum class Key(val code: Int) {
    // ── Alphanumeric ─────────────────────────────────────────────────────────
    Alpha0(GLFW_KEY_0), Alpha1(GLFW_KEY_1), Alpha2(GLFW_KEY_2), Alpha3(GLFW_KEY_3),
    Alpha4(GLFW_KEY_4), Alpha5(GLFW_KEY_5), Alpha6(GLFW_KEY_6), Alpha7(GLFW_KEY_7),
    Alpha8(GLFW_KEY_8), Alpha9(GLFW_KEY_9),

    A(GLFW_KEY_A), B(GLFW_KEY_B), C(GLFW_KEY_C), D(GLFW_KEY_D), E(GLFW_KEY_E),
    F(GLFW_KEY_F), G(GLFW_KEY_G), H(GLFW_KEY_H), I(GLFW_KEY_I), J(GLFW_KEY_J),
    K(GLFW_KEY_K), L(GLFW_KEY_L), M(GLFW_KEY_M), N(GLFW_KEY_N), O(GLFW_KEY_O),
    P(GLFW_KEY_P), Q(GLFW_KEY_Q), R(GLFW_KEY_R), S(GLFW_KEY_S), T(GLFW_KEY_T),
    U(GLFW_KEY_U), V(GLFW_KEY_V), W(GLFW_KEY_W), X(GLFW_KEY_X), Y(GLFW_KEY_Y), Z(GLFW_KEY_Z),

    // ── Signs and Punctuation ────────────────────────────────────────────────
    Space(GLFW_KEY_SPACE), Apostrophe(GLFW_KEY_APOSTROPHE), Comma(GLFW_KEY_COMMA),
    Minus(GLFW_KEY_MINUS), Period(GLFW_KEY_PERIOD), Slash(GLFW_KEY_SLASH),
    Semicolon(GLFW_KEY_SEMICOLON), Equal(GLFW_KEY_EQUAL),
    LeftBracket(GLFW_KEY_LEFT_BRACKET), Backslash(GLFW_KEY_BACKSLASH),
    RightBracket(GLFW_KEY_RIGHT_BRACKET), GraveAccent(GLFW_KEY_GRAVE_ACCENT),
    World1(GLFW_KEY_WORLD_1), World2(GLFW_KEY_WORLD_2),

    // ── Navigation and Editing ───────────────────────────────────────────────
    Escape(GLFW_KEY_ESCAPE), Enter(GLFW_KEY_ENTER), Tab(GLFW_KEY_TAB),
    Backspace(GLFW_KEY_BACKSPACE), Insert(GLFW_KEY_INSERT), Delete(GLFW_KEY_DELETE),
    PageUp(GLFW_KEY_PAGE_UP), PageDown(GLFW_KEY_PAGE_DOWN), Home(GLFW_KEY_HOME), End(GLFW_KEY_END),

    // ── Directional Arrows ───────────────────────────────────────────────────
    Right(GLFW_KEY_RIGHT), Left(GLFW_KEY_LEFT), Down(GLFW_KEY_DOWN), Up(GLFW_KEY_UP),

    // ── Modifiers and System ─────────────────────────────────────────────────
    LeftShift(GLFW_KEY_LEFT_SHIFT), LeftControl(GLFW_KEY_LEFT_CONTROL),
    LeftAlt(GLFW_KEY_LEFT_ALT), LeftSuper(GLFW_KEY_LEFT_SUPER),
    RightShift(GLFW_KEY_RIGHT_SHIFT), RightControl(GLFW_KEY_RIGHT_CONTROL),
    RightAlt(GLFW_KEY_RIGHT_ALT), RightSuper(GLFW_KEY_RIGHT_SUPER),
    Menu(GLFW_KEY_MENU),
    CapsLock(GLFW_KEY_CAPS_LOCK), ScrollLock(GLFW_KEY_SCROLL_LOCK), NumLock(GLFW_KEY_NUM_LOCK),
    PrintScreen(GLFW_KEY_PRINT_SCREEN), Pause(GLFW_KEY_PAUSE),

    // ── Function Keys ────────────────────────────────────────────────────────
    F1(GLFW_KEY_F1), F2(GLFW_KEY_F2), F3(GLFW_KEY_F3), F4(GLFW_KEY_F4), F5(GLFW_KEY_F5),
    F6(GLFW_KEY_F6), F7(GLFW_KEY_F7), F8(GLFW_KEY_F8), F9(GLFW_KEY_F9), F10(GLFW_KEY_F10),
    F11(GLFW_KEY_F11), F12(GLFW_KEY_F12), F13(GLFW_KEY_F13), F14(GLFW_KEY_F14),
    F15(GLFW_KEY_F15), F16(GLFW_KEY_F16), F17(GLFW_KEY_F17), F18(GLFW_KEY_F18),
    F19(GLFW_KEY_F19), F20(GLFW_KEY_F20), F21(GLFW_KEY_F21), F22(GLFW_KEY_F22),
    F23(GLFW_KEY_F23), F24(GLFW_KEY_F24), F25(GLFW_KEY_F25),

    // ── Numpad ───────────────────────────────────────────────────────────────
    NumPad0(GLFW_KEY_KP_0), NumPad1(GLFW_KEY_KP_1), NumPad2(GLFW_KEY_KP_2), NumPad3(GLFW_KEY_KP_3),
    NumPad4(GLFW_KEY_KP_4), NumPad5(GLFW_KEY_KP_5), NumPad6(GLFW_KEY_KP_6), NumPad7(GLFW_KEY_KP_7),
    NumPad8(GLFW_KEY_KP_8), NumPad9(GLFW_KEY_KP_9), NumPadDecimal(GLFW_KEY_KP_DECIMAL),
    NumPadDivide(GLFW_KEY_KP_DIVIDE), NumPadMultiply(GLFW_KEY_KP_MULTIPLY),
    NumPadSubtract(GLFW_KEY_KP_SUBTRACT), NumPadAdd(GLFW_KEY_KP_ADD),
    NumPadEnter(GLFW_KEY_KP_ENTER), NumPadEqual(GLFW_KEY_KP_EQUAL),
}

enum class Axis { Horizontal, Vertical }
enum class Player { P1, P2 }

private data class KeyMap(
    val up: Key,
    val down: Key,
    val left: Key,
    val right: Key
)

private val playerKeys = mapOf(
    Player.P1 to KeyMap(up = Key.W,  down = Key.S,    left = Key.A,    right = Key.D),
    Player.P2 to KeyMap(up = Key.Up, down = Key.Down, left = Key.Left, right = Key.Right)
)

object Input {

    private const val ACCELERATION = 5.0f
    private const val FRICTION     = 100.0f

    private val axes = Array(Player.entries.size) { Vec2() }

    private val currentKeys  = BooleanArray(GLFW_KEY_LAST + 1)
    private val previousKeys = BooleanArray(GLFW_KEY_LAST + 1)

    lateinit var window: Window
    private var initializedCallbacks = false

    fun init(window: Window) {
        this.window = window
    }

    fun update(delta: Float) {
        if (!initializedCallbacks) {
            setupCallbacks(window)
            initializedCallbacks = true
        }

        Mouse.clearDeltas()
        Mouse.update(window)

        currentKeys.copyInto(previousKeys)

        for (i in currentKeys.indices) {
            currentKeys[i] = glfwGetKey(window.handle, i) == GLFW_PRESS
        }

        for (player in Player.entries) {
            val a = axes[player.ordinal]
            val k = playerKeys[player] ?: continue

            a.x = axis(current = a.x, delta = delta, neg = k.left,  pos = k.right)
            a.y = axis(current = a.y, delta = delta, neg = k.down,  pos = k.up)
        }
    }

    private fun setupCallbacks(window: Window) {
        glfwSetScrollCallback(window.handle) { _, xOffset, yOffset ->
            Mouse.onScroll(xOffset.toFloat(), yOffset.toFloat())
        }
    }

    fun getAxis(player: Player, axis: Axis): Float {
        return when (axis) {
            Axis.Horizontal -> axes[player.ordinal].x
            Axis.Vertical   -> axes[player.ordinal].y
        }
    }

    fun getAxis(player: Player): Vec2 {
        return axes[player.ordinal]
    }

    // ── Keyboard ──────────────────────────────────────────────────────────────

    object Keyboard {
        fun isPressed(key: Key): Boolean = currentKeys[key.code]
        fun isJustPressed(key: Key): Boolean = currentKeys[key.code] && !previousKeys[key.code]
        fun isJustReleased(key: Key): Boolean = !currentKeys[key.code] && previousKeys[key.code]
    }

    // ── Mouse ─────────────────────────────────────────────────────────────────

    object Mouse {
        enum class MouseButton(val number: Int) { Left(0), Right(1) }

        private var _position     = Vec2()
        private var _delta        = Vec2()
        private var _lastPosition = Vec2()
        private var _scroll       = Vec2()
        private var isFirstFrame  = true // Evita saltos bruscos de cámara al inicio

        val position: Vec2 get() = _position
        val delta:    Vec2 get() = _delta
        val scroll:   Vec2 get() = _scroll

        var captured: Boolean = false
            set(value) {
                field = value
                val mode = if (value) GLFW_CURSOR_DISABLED else GLFW_CURSOR_NORMAL
                glfwSetInputMode(window.handle, GLFW_CURSOR, mode)

                isFirstFrame = true
            }

        fun isButtonPressed(button: MouseButton): Boolean = isButtonPressed(button.number)
        fun isButtonPressed(button: Int): Boolean = glfwGetMouseButton(window.handle, button) == GLFW_PRESS

        internal fun update(window: Window) {
            val xArr = DoubleArray(1)
            val yArr = DoubleArray(1)
            glfwGetCursorPos(window.handle, xArr, yArr)

            _position.x = xArr[0].toFloat()
            _position.y = yArr[0].toFloat()

            if (isFirstFrame) {
                _lastPosition.x = _position.x
                _lastPosition.y = _position.y
                isFirstFrame = false
            }

            _delta.x = _position.x - _lastPosition.x
            _delta.y = _position.y - _lastPosition.y

            _lastPosition.x = _position.x
            _lastPosition.y = _position.y
        }

        internal fun clearDeltas() {
            _delta = Vec2(0f, 0f)
            _scroll = Vec2(0f, 0f)
        }

        internal fun onScroll(xOffset: Float, yOffset: Float) {
            _scroll.x += xOffset
            _scroll.y += yOffset
        }
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private fun axis(current: Float, delta: Float, neg: Key, pos: Key): Float {
        var target = 0f
        if (Keyboard.isPressed(pos)) target += 1f
        if (Keyboard.isPressed(neg)) target -= 1f

        val smoothingSpeed = 15.0f
        return moveToward(current, target, smoothingSpeed * delta)
    }

    private fun moveToward(current: Float, target: Float, step: Float): Float {
        val d = target - current
        return if (abs(d) <= step) target else current + sign(d) * step
    }
}