package game.scenes

import engine.behaviours.SceneChanger
import engine.components.Camera
import engine.components.MeshRenderer
import engine.rendering.bindables.Material
import engine.assets.Assets
import engine.scenes.Scene
import engine.utils.PrimitiveMeshes
import glm_.quat.Quat
import glm_.vec3.Vec3
import engine.components.behaviours.FirstPersonController
import engine.utils.eulerAnglesDeg

class Scene2 : Scene() {
    override fun create() {
        entity("MainCamera") {
            component<Camera>()
            component<FirstPersonController>()
            component<SceneChanger>()
            transform.localPosition = Vec3(0f, 25f, -40f)
        }

        entity("GridFloor") {
            component<MeshRenderer> {
                mesh     = PrimitiveMeshes.plane
                material = Material(Assets.loadShader("shaders/shd_grid.glsl"))
            }
            transform.localScale = Vec3(1000f, 1f, 1000f)
        }

        entity("Model") {
            Assets.loadModel("models/model_watercolor_bird.glb").instantiate(this)
            transform.localRotation = Quat.eulerAnglesDeg(-90f, 0f, 0f)
            transform.localPosition = Vec3(0f, 1f, 0f)
        }
    }
}