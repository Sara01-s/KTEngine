package engine.scenes

import engine.behaviours.SceneChanger
import engine.components.Camera
import engine.components.Camera.BackgroundMode
import engine.components.MeshRenderer
import engine.rendering.bindables.Material
import engine.assets.Assets
import engine.utils.Color
import engine.utils.PrimitiveMeshes
import engine.utils.fromEulerAngles
import glm_.quat.Quat
import glm_.vec3.Vec3
import org.sara01.behaviours.FirstPersonController

class Scene2 : Scene() {
    override fun start() {
        entity("MainCamera") {
            component<Camera> {
                backgroundMode = BackgroundMode.SolidColor
                setBackgroundColor(Color.gray30)
            }
            component<FirstPersonController>()
            component<SceneChanger>()
            transform.localPosition = Vec3(0f, 25f, -40f)
        }

        entity("GridFloor") {
            component<MeshRenderer> {
                mesh     = PrimitiveMeshes.plane
                material = Material(Assets.loadShader("engine_assets/shaders/shd_grid.glsl"))
            }
            transform.localScale = Vec3(1000f, 1f, 1000f)
        }

        entity("Model") {
            Assets.loadModel("models/model_watercolor_bird.glb").instantiate(this)
            transform.localRotation = Quat.fromEulerAngles(-90f, 0f, 0f)
            transform.localPosition = Vec3(0f, 1f, 0f)
        }
    }
}