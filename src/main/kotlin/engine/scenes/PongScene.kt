package engine.scenes

import engine.components.AudioSource
import engine.components.Collider2D
import engine.components.MeshRenderer
import engine.game.Entity
import engine.game.Input
import engine.game.Player
import engine.game.Time
import engine.utils.normalized
import engine.rendering.Window
import engine.rendering.text.TextRenderer
import engine.systems.Assets
import engine.systems.SceneSystem
import engine.utils.log
import glm_.vec3.Vec3
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
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

// TODO: See why Vec3(x, y) needs third parameter to be 0f.

class PongScene : Scene() {

    private val p1 = createEntity().apply {
        addComponent<MeshRenderer>()
        addComponent<Collider2D>()
    }

    private val p2 = createEntity().apply {
        addComponent<MeshRenderer>()
        addComponent<Collider2D>()
    }

    private val ball = createEntity().apply {
        addComponent<MeshRenderer>().also { it.material.setTexture(Assets.loadTexture("/textures/tex_circle.png")) }
        addComponent<Collider2D>()
    }

    private val p1ScoreText = createEntity().apply {
        addComponent<TextRenderer>().text = "0"
        transform.position = Vec3(-7f, 7f)
    }

    private val p2ScoreText = createEntity().apply {
        addComponent<TextRenderer>().text = "0"
        transform.position = Vec3(7f, 7f)
    }

    private val musicSource = createEntity().apply {
        addComponent<AudioSource>().also {
            it.clip = Assets.loadAudioClip("/audio/music_periwinkle.wav")
            it.loop = true
            it.volume = 0.4f
        }.play()
    }

    private val sfxSource = createEntity().apply {
        addComponent<AudioSource>().also {
            it.volume = 0.5f
        }
    }

    private var ballVelocity = Vec3()
    private var scoreP1 = 0
    private var scoreP2 = 0

    init {
        ball.transform.scale = Vec3(0.75f)

        p1.transform.position = Vec3(-12f, 0f, 0f)
        p1.transform.scale = Vec3(PAD_EXTENT_X * 2, PAD_EXTENT_Y * 2)

        p2.transform.position = Vec3(12f, 0f, 0f)
        p2.transform.scale = Vec3(PAD_EXTENT_X * 2, PAD_EXTENT_Y * 2)

        p1.getComponent<Collider2D>().onEnter = { reflectOnPad(p1, 1f) }
        p2.getComponent<Collider2D>().onEnter = { reflectOnPad(p2, -1f) }

        playSfx("/audio/sfx_spawn.wav")
        resetBall()
    }

    override fun fixedUpdate() {
        val bounds = calculateWorldBounds()

        ball.transform.position = ball.transform.position.plus(ballVelocity * Time.FIXED_DELTA_TIME)

        // Top/Bottom bounce.
        if (ball.transform.position.y > bounds.y) {
            playSfx("/audio/sfx_hit.wav")

            ball.transform.position.y = bounds.y
            ballVelocity.y *= -1f
        }

        if (ball.transform.position.y < -bounds.y) {
            playSfx("/audio/sfx_hit.wav")

            ball.transform.position.y = -bounds.y
            ballVelocity.y *= -1f
        }

        // Score.
        if (ball.transform.position.x > bounds.x) {
            scoreP2++
            p1ScoreText.getComponent<TextRenderer>().text = "$scoreP2"
            playSfx("/audio/sfx_score.wav")

            resetBall(-1f)
        }

        if (ball.transform.position.x < -bounds.x) {
            scoreP1++
            p2ScoreText.getComponent<TextRenderer>().text = "$scoreP1"
            playSfx("/audio/sfx_score.wav")

            resetBall(1f)
        }
    }

    override fun update() {
        if (Input.isKeyJustPressed(GLFW_KEY_ESCAPE)) {
            SceneSystem.loadScene(MainMenuScene())
        }

        val bounds = calculateWorldBounds()

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

    private fun playSfx(path: String) {
        val source = sfxSource.getComponent<AudioSource>()

        source.stop()
        source.clip = Assets.loadAudioClip(path)
        source.play()
    }

    private fun resetBall(dir: Float = 1f) {
        ball.transform.position = Vec3(0f)

        val angle = (Math.random() * 0.5 - 0.25).toFloat()

        ballVelocity = Vec3(
            dir * BALL_SPEED,
            sin(angle.toDouble()).toFloat() * BALL_SPEED
        )

        log("Score — P1: $scoreP1  P2: $scoreP2")
    }

    private fun reflectOnPad(pad: Entity, sideDir: Float) {
        playSfx("/audio/sfx_hit.wav")

        val rel = (ball.transform.position.y - pad.transform.position.y) / PAD_EXTENT_Y
        val angle = rel * (Math.PI / 3.5)

        val speed = minOf(
            sqrt((ballVelocity.x * ballVelocity.x + ballVelocity.y * ballVelocity.y).toDouble()).toFloat() * BALL_ACCEL,
            BALL_MAX_SPEED
        )

        ballVelocity = Vec3(
            sideDir * cos(angle).toFloat() * speed,
            sin(angle).toFloat() * speed
        )
    }

    private fun calculateWorldBounds(): Vec3 {
        return Vec3(Window.aspectRatio * 10f, 10f)
    }
}