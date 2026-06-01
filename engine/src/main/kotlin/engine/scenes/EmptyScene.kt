package engine.scenes

import engine.assets.Assets
import engine.assets.DefaultAssets
import engine.components.Camera
import engine.components.MeshRenderer
import engine.components.behaviours.DirectionalLight
import engine.components.behaviours.FirstPersonController
import engine.rendering.Skybox
import engine.rendering.bindables.Material
import engine.utils.Layers
import engine.utils.PrimitiveMeshes
import engine.utils.Tags
import engine.utils.eulerAnglesDeg
import glm_.quat.Quat
import glm_.vec3.Vec3

class EmptyScene : Scene() {
    override fun create() {
        entity("Grid Floor") {
            transform.localScale = Vec3(1000f, 1f, 1000f)
            layerMask = Layers.SCENE

            component<MeshRenderer> {
                mesh     = PrimitiveMeshes.plane
                material = Material(Assets.loadShader("shaders/shd_grid.glsl"))
            }
        }

        val cube = entity("Default Cube") {
            component<MeshRenderer> {
                mesh = PrimitiveMeshes.cube
                material = DefaultAssets.litMaterial
            }
        }

        entity("Main Camera") {
            tag = Tags.MAIN_CAMERA

            transform.worldPosition = Vec3(5f, 2.5f, -5f)
            transform.lookAt(cube.transform)

            component<Camera>().apply {
                cullingMask = Layers.EVERYTHING and Layers.SCENE.inv()
            }
        }

        entity("Scene Camera") {
            tag = Tags.SCENE_CAMERA

            transform.worldPosition = Vec3(5f, 2.5f, -5f)
            transform.lookAt(cube.transform)

            component<FirstPersonController>()
            component<Camera> {
                backgroundMode = Camera.BackgroundMode.SkyBox
                skybox = Skybox()
            }
        }

        entity("Directional Light") {
            component<DirectionalLight>()
            transform.localRotation = Quat.eulerAnglesDeg(270f, 50f, 0f)
        }
    }
}