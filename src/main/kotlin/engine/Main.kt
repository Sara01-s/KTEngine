package engine

import engine.game.Entity
import engine.game.Game
import engine.game.Input
import engine.game.Player
import engine.game.Time
import engine.math.normalized
import engine.renderer.Color
import engine.renderer.Renderer
import engine.renderer.Window
import engine.renderer.bindables.Shader
import engine.renderer.drawables.Square
import engine.utils.logInfo
import glm_.vec2.Vec2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Game Settings.
const val SCREEN_WIDTH   = 1280f
const val SCREEN_HEIGHT  = 720f
const val PAD_SPEED      = 20f
const val PAD_EXTENT_X   = 0.3f
const val PAD_EXTENT_Y   = 1.5f
const val BALL_SPEED     = 9f
const val BALL_ACCEL     = 1.05f
const val BALL_MAX_SPEED = 80f

fun main() {
    logInfo("Creating Window.")
    val window = Window(SCREEN_WIDTH.toInt(), SCREEN_HEIGHT.toInt(), "Pong")

    logInfo("Creating Renderer.")
    val renderer = Renderer()
    renderer.setClearColor(Color.coolPurple)

    logInfo("Creating Game.")
    val game = Game(window, renderer)

    val circleShader = Shader("/shaders/circle_vsh.glsl", "/shaders/circle_fsh.glsl")
    val rectShader = Shader("/shaders/rect_vsh.glsl", "/shaders/rect_fsh.glsl")

    val p1   = Entity(drawable = Square(rectShader, vertexColor = Color.magenta))
    val p2   = Entity(drawable = Square(rectShader, vertexColor = Color.magenta))
    val ball = Entity(drawable = Square(circleShader, vertexColor = Color.yellow))

    ball.transform.scale = Vec2(0.75f, 0.75f)

    p1.transform.position = Vec2(-12f, 0f)
    p1.transform.scale   = Vec2(PAD_EXTENT_X * 2, PAD_EXTENT_Y * 2)

    p2.transform.position = Vec2( 12f, 0f)
    p2.transform.scale = Vec2(PAD_EXTENT_X * 2, PAD_EXTENT_Y * 2)

    var scoreP1 = 0
    var scoreP2 = 0

    var ballVelocity = Vec2(0f, 0f)

    fun resetBall(dir: Float = 1f) {
        ball.transform.position = Vec2(0f, 0f)

        val angle = (Math.random() * 0.5 - 0.25).toFloat()
        ballVelocity = Vec2(dir * BALL_SPEED, sin(angle.toDouble()).toFloat() * BALL_SPEED)

        logInfo("Score — P1: $scoreP1  P2: $scoreP2")
    }

    fun reflectOnPad(pad: Entity, sideDir: Float) {
        val rel   = (ball.transform.position.y - pad.transform.position.y) / PAD_EXTENT_Y
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

    fun worldBounds(): Vec2 {
        return Vec2(Window.aspectRatio * 10f, 10f)
    }

    p1.collider.onEnter = { reflectOnPad(p1,  1f) }
    p2.collider.onEnter = { reflectOnPad(p2, -1f) }

    resetBall()

    game.loop(
        fixedUpdate = {
            val bounds = worldBounds()

            p1.collider.update(listOf(ball.collider))
            p2.collider.update(listOf(ball.collider))

            ball.transform.position = ball.transform.position + ballVelocity * Time.FIXED_DELTA_TIME

            // World bounds bounce.
            if (ball.transform.position.y >  bounds.y) {
                ball.transform.position = Vec2(ball.transform.position.x,  bounds.y)
                ballVelocity = Vec2(ballVelocity.x, -ballVelocity.y)
            }
            if (ball.transform.position.y < -bounds.y) {
                ball.transform.position = Vec2(ball.transform.position.x, -bounds.y)
                ballVelocity = Vec2(ballVelocity.x, -ballVelocity.y)
            }

            // Score.
            if (ball.transform.position.x >  bounds.x) { 3
                scoreP2++
                resetBall(-1f)
            }

            if (ball.transform.position.x < -bounds.x) {
                scoreP1++
                resetBall( 1f)
            }
        },
        update = {
            val bounds = worldBounds()

            val axisP1 = Input.getAxis(Player.P1).normalized()
            val axisP2 = Input.getAxis(Player.P2).normalized()

            p1.transform.position = Vec2(
                p1.transform.position.x,
                (p1.transform.position.y + axisP1.y * PAD_SPEED * Time.deltaTime)
                    .coerceIn(-bounds.y + PAD_EXTENT_Y, bounds.y - PAD_EXTENT_Y)
            )
            p2.transform.position = Vec2(
                p2.transform.position.x,
                (p2.transform.position.y + axisP2.y * PAD_SPEED * Time.deltaTime)
                    .coerceIn(-bounds.y + PAD_EXTENT_Y, bounds.y - PAD_EXTENT_Y)
            )
        },
        draw = {
            p1.drawable.draw()
            p2.drawable.draw()
            ball.drawable.draw()
        }
    )

    p1.close()
    p2.close()
    ball.close()
    window.close()

    logInfo("Bye Bye.")
}