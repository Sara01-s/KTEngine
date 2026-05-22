package engine.components

import engine.game.Entity
import engine.rendering.bindables.Texture
import engine.systems.Assets
import engine.systems.RenderSystem
import engine.utils.Color
import engine.utils.PrimitiveMeshes

class MeshRenderer : Renderer {
    override lateinit var entity: Entity
    override var isVisible = true

    var mesh = PrimitiveMeshes.quad
    val material = Assets.loadDefaultMaterial()

    var color = Color.white
        set(value) {
            field = value
            material.setColor("_ColorTint", value)
        }

    var texture: Texture? = null
        set(value) {
            field = value
            if (value != null) {
                material.setTexture("_MainTex", value)
            } else {
                error("Cannot assign a null texture to MeshRenderer")
            }
        }

    override fun onAdded() {
        RenderSystem.register(this)
    }

    override fun onRemoved() {
        RenderSystem.unregister(this)
    }

    override fun draw() {
        if (!isVisible) return

        material.bind()
        material.setMat4("_MVP", RenderSystem.calculateMvpMatrix(entity.transform))
        mesh.draw()
    }

    override fun close() {
        material.close()
        onRemoved()
    }
}