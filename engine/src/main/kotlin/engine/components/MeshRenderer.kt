package engine.components

import engine.assets.DefaultAssets
import engine.systems.CameraSystem
import engine.systems.LightingSystem
import engine.systems.RenderSystem
import engine.utils.PrimitiveMeshes

class MeshRenderer : Renderer() {
    var mesh = PrimitiveMeshes.quad
    var material = DefaultAssets.material

    // TODO: Detected whether is an Lit or Unlit material to expose this fields.
    var metallicIntensity = 0f
    var roughnessIntensity = 0.7f

    override fun onAdded() {
        RenderSystem.register(this)
    }

    override fun onRemoved() {
        RenderSystem.unregister(this)
    }

    override fun draw() {
        val cameraTransform = CameraSystem.main!!.entity.transform  // TODO: Check for null camera.

        material.bind()

        material.setMat4("_MVP", RenderSystem.calculateMvpMatrix(entity.transform))
        material.setMat4("_ModelMatrix", RenderSystem.calculateModelMatrix(entity.transform))
        material.setMat4("_ViewMatrix", RenderSystem.calculateViewMatrix(cameraTransform))
        material.setVec3("_CameraPosition", cameraTransform.worldPosition)

        if (LightingSystem.isEnabled()) {
            val sun = LightingSystem.directionalLight!!
            material.setVec3("_LightDirection", sun.transform.forward)
            material.setFloat("_LightIntensity", sun.intensity)
            material.setColor3("_LightColor", sun.color)
        }

        material.setFloat("_MetallicIntensity", metallicIntensity)
        material.setFloat("_RoughnessIntensity", roughnessIntensity)

        mesh.draw()
    }

    override fun close() {
        material.close()
        onRemoved()
    }
}