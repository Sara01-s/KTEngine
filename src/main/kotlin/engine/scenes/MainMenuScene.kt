package engine.scenes

import engine.game.Assets
import engine.game.Scene
import engine.renderer.drawables.Square
import glm_.vec2.Vec2

class MainMenuScene : Scene() {
    private val square = createEntity(drawable = Square(shader = createShader("/shaders/texture_vsh.glsl", "/shaders/texture_fsh.glsl")))

    init {
        val texture = Assets.loadTexture("/textures/tex_test.png")
        square.drawable.shader.setTexture("u_Texture", texture)
    }

    override fun update() {
        square.transform.scale = Vec2(10f, 10f)
    }

    override fun draw() {
        square.drawable.draw()
    }
}