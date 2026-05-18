package engine.scenes

import engine.game.Assets
import engine.game.Entity
import engine.game.Input
import engine.game.Player
import engine.game.Scene
import engine.game.Time
import engine.math.normalized
import engine.renderer.Color
import engine.renderer.Window
import engine.renderer.bindables.Texture
import engine.renderer.drawables.Square
import engine.utils.log
import glm_.vec2.Vec2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Game Settings.
const val PAD_SPEED = 20f
const val PAD_EXTENT_X = 0.3f
const val PAD_EXTENT_Y = 1.5f
const val BALL_SPEED = 9f
const val BALL_ACCEL = 1.05f
const val BALL_MAX_SPEED = 80f

class PongScene : Scene() {

    private val circleShader =
        Assets.loadShader(
            "/shaders/circle_vsh.glsl",
            "/shaders/circle_fsh.glsl"
        )

    private val rectShader =
        Assets.loadShader(
            "/shaders/rect_vsh.glsl",
            "/shaders/rect_fsh.glsl"
        )

    private val textureShader =
        Assets.loadShader(
            "/shaders/texture_vsh.glsl",
            "/shaders/texture_fsh.glsl"
        )

    private val p1 = createEntity(
        drawable = Square(textureShader, vertexColor = Color.magenta)
    )

    private val p2 = createEntity(
        drawable = Square(textureShader, vertexColor = Color.magenta)
    )

    private val ball = createEntity(
        drawable = Square(circleShader, vertexColor = Color.yellow)
    )

    private var scoreP1 = 0
    private var scoreP2 = 0

    private var ballVelocity = Vec2()

    init {
        ball.transform.scale = Vec2(0.75f)
        p1.drawable.shader.setTexture("u_Texture", Assets.loadTexture("/textures/tex_test.png"))
        p2.drawable.shader.setTexture("u_Texture", Assets.loadTexture("/textures/tex_test.png"))

        p1.transform.position = Vec2(-12f, 0f)
        p1.transform.scale = Vec2(PAD_EXTENT_X * 2, PAD_EXTENT_Y * 2)

        p2.transform.position = Vec2(12f, 0f)
        p2.transform.scale = Vec2(PAD_EXTENT_X * 2, PAD_EXTENT_Y * 2)

        p1.collider.onEnter = { reflectOnPad(p1, 1f) }
        p2.collider.onEnter = { reflectOnPad(p2, -1f) }

        resetBall()
    }

    override fun fixedUpdate() {
        val bounds = worldBounds()

        p1.collider.update(listOf(ball.collider))
        p2.collider.update(listOf(ball.collider))

        ball.transform.position = ball.transform.position.plus(ballVelocity * Time.FIXED_DELTA_TIME)

        // Top/Bottom bounce.
        if (ball.transform.position.y > bounds.y) {
            ball.transform.position.y = bounds.y
            ballVelocity.y *= -1f
        }

        if (ball.transform.position.y < -bounds.y) {
            ball.transform.position.y = -bounds.y
            ballVelocity.y *= -1f
        }

        // Score.
        if (ball.transform.position.x > bounds.x) {
            scoreP2++
            resetBall(-1f)
        }

        if (ball.transform.position.x < -bounds.x) {
            scoreP1++
            resetBall(1f)
        }
    }

    override fun update() {
        val bounds = worldBounds()

        val axisP1 = Input.getAxis(Player.P1).normalized()
        val axisP2 = Input.getAxis(Player.P2).normalized()

        p1.transform.position.y =
            (p1.transform.position.y +
                    axisP1.y * PAD_SPEED * Time.deltaTime)
                .coerceIn(
                    -bounds.y + PAD_EXTENT_Y,
                    bounds.y - PAD_EXTENT_Y
                )

        p2.transform.position.y =
            (p2.transform.position.y +
                    axisP2.y * PAD_SPEED * Time.deltaTime)
                .coerceIn(
                    -bounds.y + PAD_EXTENT_Y,
                    bounds.y - PAD_EXTENT_Y
                )
    }

    override fun draw() {
        p1.drawable.draw()
        p2.drawable.draw()
        ball.drawable.draw()
    }

    private fun resetBall(dir: Float = 1f) {
        ball.transform.position = Vec2(0f)

        val angle =
            (Math.random() * 0.5 - 0.25).toFloat()

        ballVelocity = Vec2(
            dir * BALL_SPEED,
            sin(angle.toDouble()).toFloat() * BALL_SPEED
        )

        log("Score — P1: $scoreP1  P2: $scoreP2")
    }

    private fun reflectOnPad(pad: Entity, sideDir: Float) {
        val rel = (ball.transform.position.y - pad.transform.position.y) / PAD_EXTENT_Y
        val angle = rel * (Math.PI / 3.5)

        val speed = minOf(
            sqrt((ballVelocity.x * ballVelocity.x + ballVelocity.y * ballVelocity.y).toDouble()).toFloat() * BALL_ACCEL,
            BALL_MAX_SPEED
        )

        ballVelocity = Vec2(
            sideDir * cos(angle).toFloat() * speed,
            sin(angle).toFloat() * speed
        )
    }

    private fun worldBounds(): Vec2 {
        return Vec2(Window.aspectRatio * 10f, 10f)
    }
}