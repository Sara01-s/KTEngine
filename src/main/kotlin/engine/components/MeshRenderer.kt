package engine.components

import engine.systems.Assets
import engine.systems.CameraSystem
import engine.systems.RenderSystem
import engine.utils.PrimitiveMeshes

class MeshRenderer : Renderer() {
    var mesh = PrimitiveMeshes.quad
    var material = Assets.loadDefaultMaterial()

    override fun onAdded() {
        RenderSystem.register(this)
    }

    override fun onRemoved() {
        RenderSystem.unregister(this)
    }

    override fun draw() {
        val cameraTransform = CameraSystem.main!!.entity.transform  // TODO: Check for null camera.

        material.setMat4("_MVP", RenderSystem.calculateMvpMatrix(entity.transform))
        material.setMat4("_ModelMatrix", RenderSystem.calculateModelMatrix(entity.transform))
        material.setMat4("_ViewMatrix", RenderSystem.calculateViewMatrix(cameraTransform))
        material.setVec3("_CameraPosition", cameraTransform.worldPosition)

        material.bind()
        mesh.draw()
    }

    override fun close() {
        material.close()
        onRemoved()
    }
}