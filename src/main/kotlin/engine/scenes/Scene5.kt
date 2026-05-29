package engine.scenes

import engine.components.Camera
import engine.components.Camera.BackgroundMode
import engine.components.MeshRenderer
import engine.components.Transform
import engine.components.FirstPersonController
import engine.game.Time
import engine.game.Model
import engine.rendering.Skybox
import engine.rendering.bindables.Material
import engine.systems.Assets
import engine.systems.Input
import engine.systems.Key
import engine.systems.SceneSystem
import engine.utils.Color
import engine.utils.PrimitiveMeshes
import engine.utils.fromEulerAngles
import glm_.quat.Quat
import glm_.vec3.Vec3

class Scene5 : Scene() {
    private val cameraEntity by entityRef("MainCamera")

    private var targetTransform: Transform? = null
    private val rotationSpeed = 1.0f

    override fun start() {
        Input.Mouse.captured = true

        entity("MainCamera") {
            addComponent<Camera>().apply {
                backgroundMode = BackgroundMode.SolidColor
                setBackgroundColor(Color.teal)
            }
            addComponent<FirstPersonController>()
            transform.localPosition = Vec3(0f, 25f, -40f)
        }

        entity("GridFloor") {
            addComponent<MeshRenderer>().also {
                it.mesh = PrimitiveMeshes.plane
                it.material = Material(Assets.loadShader("/shaders/shd_grid.glsl"))
            }
            transform.localScale = Vec3(1000f, 1f, 1000f)
        }

        entity("Model") {
            val model = Assets.loadModel("/models/model_watercolor_bird.glb")
            model.instantiate(this)
            transform.localRotation = Quat.fromEulerAngles(-90f, 90f, 0f)
            transform.localScale = Vec3(0.1f)
        }
    }

    override fun update() {
        cameraEntity.getComponent<FirstPersonController>().update()

        targetTransform?.let {
            val rotationDelta = rotationSpeed * Time.deltaTime
            it.rotate(rotationDelta, rotationDelta)
        }

        val sceneMap = mapOf(
            Key.Alpha1 to { Scene1() },
            Key.Alpha2 to { Scene2() },
            Key.Alpha3 to { Scene3() },
            Key.Alpha4 to { Scene4() },
            Key.Alpha5 to { Scene5() },
            Key.Alpha6 to { Scene6() }
        )

        sceneMap.forEach { (key, sceneFactory) ->
            if (Input.Keyboard.isJustPressed(key)) {
                SceneSystem.loadScene(sceneFactory())
            }
        }
    }
}