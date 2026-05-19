package engine.scenes

import engine.components.AudioSource
import engine.components.SpriteRenderer
import engine.game.Input
import engine.game.Time
import engine.rendering.text.TextRenderer
import engine.systems.Assets
import engine.systems.SceneSystem
import glm_.vec2.Vec2
import org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE

class MainMenuScene : Scene() {
    private val pong = createEntity().apply {
        addComponent<TextRenderer>().text = "PONG"
        transform.position = Vec2(-5.5f, 7f)
        transform.scale = Vec2(3f)
    }

    private val start = createEntity().apply {
        addComponent<TextRenderer>().text = "Press [Space] to start the game!"
        transform.position = Vec2(-7f, 1f)
        transform.scale = Vec2(0.5f)
    }

    private val eli = createEntity().apply {
        addComponent<SpriteRenderer>().texture = Assets.loadTexture("/textures/tex_eli.png")
        transform.position = Vec2(-12f, -4f)
        transform.scale = Vec2(10f, 10f)
    }

    private val musicSource = createEntity().apply {
        addComponent<AudioSource>().apply {
            clip = Assets.loadAudioClip("/audio/music_june.wav")
            volume = 0.3f
            loop = true
        }.play()
    }

    override fun update() {
        val text = start.getComponent<TextRenderer>()

        text.isVisible = (Time.time % 1f) < 0.5f

        if (Input.isKeyJustPressed(GLFW_KEY_SPACE)) {
            SceneSystem.loadScene(PongScene())
        }
    }
}

