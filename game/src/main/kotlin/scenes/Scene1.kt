package game.scenes

import engine.assets.Assets
import engine.behaviours.SceneChanger
import engine.components.Camera
import engine.components.MeshRenderer
import engine.rendering.bindables.Material
import engine.assets.EngineAssets
import engine.components.behaviours.DirectionalLight
import engine.scenes.Scene
import engine.utils.PrimitiveMeshes
import glm_.quat.Quat
import glm_.vec3.Vec3
import engine.components.behaviours.FirstPersonController
import engine.utils.eulerAnglesDeg

class Scene1 : Scene() {
    override fun create() {
        entity("Sun") {
            component<DirectionalLight>().apply {
                intensity = 3f
            }
            transform.localRotation = Quat.eulerAnglesDeg(-10f, 10f, 0f)
        }

        entity("MainCamera") {
            component<Camera>()
            component<FirstPersonController>()
            component<SceneChanger>()
            transform.localPosition = Vec3(0f, 6f, -18f)
        }

        entity("GridFloor") {
            component<MeshRenderer> {
                mesh     = PrimitiveMeshes.plane
                material = Material(Assets.loadShader("shaders/shd_grid.glsl"))
            }
            transform.localScale = Vec3(1000f, 1f, 1000f)
        }

        entity("Model") {
            EngineAssets.loadModel("models/model_mech_drone.glb").instantiate(this)
            transform.localRotation = Quat.eulerAnglesDeg(-90f, 90f, 0f)
            transform.localScale = Vec3(0.1f)
            transform.localPosition = Vec3(0f, 1f, 0f)
        }
    }
}