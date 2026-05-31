package engine.scenes

import engine.assets.Assets
import engine.behaviours.SceneChanger
import engine.components.Camera
import engine.components.MeshRenderer
import engine.rendering.bindables.Material
import engine.assets.EngineAssets
import engine.components.behaviours.DirectionalLight
import engine.utils.PrimitiveMeshes
import engine.utils.fromEulerAngles
import glm_.quat.Quat
import glm_.vec3.Vec3
import org.sara01.behaviours.FirstPersonController

class Scene1 : Scene() {
    override fun start() {
        entity("Sun") {
            component<DirectionalLight>().intensity = 3f
            transform.localRotation = Quat.fromEulerAngles(-10f, 10f, 0f)
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
            transform.localRotation = Quat.fromEulerAngles(-90f, 90f, 0f)
            transform.localScale = Vec3(0.1f)
            transform.localPosition = Vec3(0f, 1f, 0f)
        }
    }
}