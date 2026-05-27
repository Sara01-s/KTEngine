package engine.scenes

import engine.components.Camera
import engine.components.MeshRenderer
import engine.components.Transform
import engine.components.FirstPersonController
import engine.game.Time
import engine.rendering.bindables.Material
import engine.systems.Assets
import engine.systems.Input
import engine.utils.PrimitiveMeshes
import glm_.vec3.Vec3

class Scene3D : Scene() {
    private val cameraEntity by entityRef("MainCamera")

    private val rotatingTransforms = mutableListOf<Transform>()
    private val rotationSpeed = 1f

    init {
        Input.Mouse.captured = true
        val uvTexture = Assets.loadTexture("/textures/tex_test_uv.png")

        entity("GridFloor") {
            transform.localScale = Vec3(1000f, 1f, 1000f)
            addComponent<MeshRenderer>().also {
                it.mesh = PrimitiveMeshes.plane
                it.material = Material(Assets.loadShader("/shaders/shd_grid.glsl"))
            }
        }

        entity("MainCamera") {
            addComponent<Camera>()
            addComponent<FirstPersonController>()
            transform.localPosition = Vec3(0f, 6f, -18f)
        }

        entity("TestPlane") {
            addComponent<MeshRenderer>().also {
                it.mesh = PrimitiveMeshes.plane
                it.material.setTexture(uvTexture)
            }

            transform.localScale = Vec3(10f, 1f, 10f)
            transform.localPosition = Vec3(0f, 0.5f, 0f)
        }

        val primitives = listOf(
            PrimitiveMeshes.quad,
            PrimitiveMeshes.cube,
            PrimitiveMeshes.sphere,
            PrimitiveMeshes.cylinder,
            PrimitiveMeshes.capsule,
        )

        val spacing = 2.5f
        val startX = -((primitives.size - 1) * spacing) / 2f

        for ((index, mesh) in primitives.withIndex()) {
            entity("PrimitiveRow_$index") {
                transform.localPosition = Vec3(startX + (index * spacing), 2.5f, 0f)

                addComponent<MeshRenderer>().also {
                    it.mesh = mesh
                    it.material = Material(Assets.loadDefaultShader()).apply {
                        setTexture(uvTexture)
                    }
                }

                rotatingTransforms.add(this.transform)

                if (index == 2) {
                    transform.childEntity("OrbitingCube") {
                        addComponent<MeshRenderer>().mesh = PrimitiveMeshes.cube
                        transform.localPosition = Vec3(0f, 2f, 0f)
                    }
                }
            }
        }
    }

    override fun update() {
        cameraEntity.getComponent<FirstPersonController>().update()

        for (transform in rotatingTransforms) {
            val rotation = rotationSpeed * Time.deltaTime
            transform.rotate(rotation, rotation)
        }
    }
}