package engine.components

import engine.game.Entity
import engine.rendering.bindables.Texture
import engine.systems.Assets
import engine.systems.CameraSystem
import engine.systems.RenderSystem
import engine.utils.Color
import engine.utils.PrimitiveMeshes

class MeshRenderer : Renderer {
    override lateinit var entity: Entity
    override var isVisible = true

    var mesh = PrimitiveMeshes.quad
    var material = Assets.loadDefaultMaterial()

    override fun onAdded() {
        RenderSystem.register(this)
    }

    override fun onRemoved() {
        RenderSystem.unregister(this)
    }

    override fun draw() {
        if (!isVisible) return

        material.setMat4("_MVP", RenderSystem.calculateMvpMatrix(entity.transform))
        material.setMat4("_ModelMatrix", RenderSystem.calculateModelMatrix(entity.transform))
        material.setVec3("_CameraPosition", CameraSystem.main!!.entity.transform.worldPosition)
        material.bind()
        mesh.draw()
    }

    override fun close() {
        material.close()
        onRemoved()
    }
}