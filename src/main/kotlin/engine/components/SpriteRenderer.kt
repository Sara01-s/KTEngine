package engine.components

import engine.game.Assets
import engine.game.Entity
import engine.renderer.Color
import engine.renderer.Renderer
import engine.renderer.drawables.Quad

class SpriteRenderer : Component {
    override lateinit var entity: Entity
    lateinit var drawable: Quad
        private set

    val material = Assets.loadDefaultMaterial()

    var color = Color.white
        set(value) {
            field = value
            material.setColor("_ColorTint", value)
        }

    var texture = material.getTexture("_MainTex")
        set(value) {
            field = value
            if (value != null) {
                material.setTexture("_MainTex", value)
            }
            else {
                error("Could not load texture")
            }
        }

    override fun onAdded() {
        drawable = Quad(entity.transform, material)
        Renderer.register(drawable)
    }

    override fun onRemoved() {
        Renderer.unregister(drawable)
    }
}