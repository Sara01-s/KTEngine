package engine.scenes

import engine.components.AudioSource
import engine.components.MeshRenderer
import engine.systems.Input
import engine.game.Time
import engine.rendering.text.TextRenderer
import engine.systems.Assets
import engine.systems.Key
import engine.systems.SceneSystem
import glm_.vec3.Vec3
import org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE

class MainMenuScene : Scene() {
    private val pong = createEntity().apply {
        addComponent<TextRenderer>().text = "PONG"
        transform.localPosition = Vec3(-5.5f, 7f)
        transform.localPosition = Vec3(3f)
    }

    private val start = createEntity().apply {
        addComponent<TextRenderer>().text = "Press [Space] to start the game!"
        transform.localPosition = Vec3(-7f, 1f)
        transform.localPosition = Vec3(0.5f)
    }

    private val eli = createEntity().apply {
        addComponent<MeshRenderer>().material.setTexture(Assets.loadTexture("/textures/tex_eli.png"))
        transform.localPosition = Vec3(-12f, -4f, 0f)
        transform.localPosition = Vec3(10f, 10f, 0f)
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

        if (Input.Keyboard.isJustPressed(Key.Space)) {
            SceneSystem.loadScene(PongScene())
        }
    }
}

