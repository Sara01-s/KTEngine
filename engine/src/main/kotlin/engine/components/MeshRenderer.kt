package engine.components

import engine.rendering.bindables.Material
import engine.systems.LightingSystem
import engine.systems.RenderSystem
import engine.utils.PrimitiveMeshes

class MeshRenderer : Renderer() {
    var mesh = PrimitiveMeshes.quad
    var material: Material
        get() = mesh.material
        set(value) {
            mesh.material = value
        }

    // TODO: Detected whether is an Lit or Unlit material to expose this fields.
    var metallicIntensity = 0f
    var roughnessIntensity = 0.7f

    override fun draw(camera: Camera, aspect: Float) {
        val modelMatrix = RenderSystem.calculateModelMatrix(entity.transform)
        val normalMatrix = modelMatrix.inverse().transpose().toMat3()

        material.bind()

        val model = RenderSystem.calculateModelMatrix(entity.transform)

        material.setMat4("_ModelMatrix", model)
        material.setMat3("_NormalMatrix", normalMatrix)

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