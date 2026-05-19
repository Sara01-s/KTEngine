package engine.components

import engine.game.Entity
import engine.rendering.bindables.Mesh
import engine.systems.Assets
import engine.systems.RenderSystem
import engine.utils.Color

class SpriteRenderer : Renderer {
    override lateinit var entity: Entity

    val mesh = Mesh.generateQuad()
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
        RenderSystem.register(this)
    }

    override fun onRemoved() {
        RenderSystem.unregister(this)
    }

    override fun draw() {
        material.bind()
        material.setMat4("_MVP", RenderSystem.calculateMvpMatrix(entity.transform))
        mesh.draw()
    }

    override fun close() {
        material.close()
        mesh.close()
    }
}