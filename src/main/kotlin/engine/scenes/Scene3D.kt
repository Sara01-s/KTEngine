package engine.scenes

import engine.components.MeshRenderer
import engine.game.Camera
import engine.rendering.bindables.Material
import engine.systems.Assets
import engine.utils.PrimitiveMeshes
import glm_.vec3.Vec3

class Scene3D : Scene() {
    val cube = createEntity().apply {
        addComponent<MeshRenderer>().also {
            it.mesh = PrimitiveMeshes.cube
            it.material = Material(Assets.loadShader("/shaders/shd_textured_rotated.glsl"))
            it.material.setTexture(Assets.loadTexture("/textures/tex_test_uv.png"))
        }
        transform.scale = Vec3(9f)
    }

    init {
        Camera.position = Vec3(0f, 0f, 10f)
    }
}