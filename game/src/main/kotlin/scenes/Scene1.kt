package engine.scenes

import engine.behaviours.SceneChanger
import engine.components.Camera
import engine.components.MeshRenderer
import engine.rendering.bindables.Material
import engine.assets.Assets
import engine.assets.DefaultAssets
import engine.assets.EngineAssets
import engine.game.Model
import engine.utils.PrimitiveMeshes
import engine.utils.fromEulerAngles
import glm_.quat.Quat
import glm_.vec3.Vec3
import org.sara01.behaviours.FirstPersonController

class Scene1 : Scene() {
    override fun start() {
        entity("MainCamera") {
            component<Camera>()
            component<FirstPersonController>()
            component<SceneChanger>()
            transform.localPosition = Vec3(0f, 6f, -18f)
        }

        entity("GridFloor") {
            component<MeshRenderer> {
                mesh     = PrimitiveMeshes.plane
                material = Material(EngineAssets.loadShader("shaders/shd_grid.glsl"))
            }
            transform.localScale = Vec3(1000f, 1f, 1000f)
        }

        entity("Model") {
            DefaultAssets.model.instantiate(this)
            transform.localRotation = Quat.fromEulerAngles(-90f, 0f, 0f)
            transform.localPosition = Vec3(0f, 1f, 0f)
        }
    }
}