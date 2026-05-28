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

class Scene6 : Scene() {
    private val cameraEntity by entityRef("MainCamera")

    private var targetTransform: Transform? = null
    private val rotationSpeed = 1.0f

    override fun start() {
        Input.Mouse.captured = true
        val uvTexture = Assets.loadTexture("/textures/tex_test_uv.png")

        entity("GridFloor") {
            addComponent<MeshRenderer>().also {
                it.mesh = PrimitiveMeshes.plane
                it.material = Material(Assets.loadShader("/shaders/shd_grid.glsl"))
            }
            transform.localScale = Vec3(1000f, 1f, 1000f)
        }

        entity("MainCamera") {
            addComponent<Camera>().apply {
                backgroundMode = BackgroundMode.SkyBox
                setSkyBox(Skybox())
            }
            addComponent<FirstPersonController>()
            transform.localPosition = Vec3(0f, 6f, -18f)
        }

        entity("TestPlane") {
            addComponent<MeshRenderer>().also {
                it.mesh = PrimitiveMeshes.plane
                it.material = Material(Assets.loadDefaultShader()).apply {
                    setTexture("_MainTex", uvTexture, 0)
                }
            }
            transform.localScale = Vec3(10f, 1f, 10f)
            transform.localPosition = Vec3(0f, 0.5f, 0f)
        }

        entity("Model") {
            val model = Assets.loadModel("/models/model_forest_house.glb")
            model.instantiate(this)
            transform.localPosition = Vec3(0f, 20f, 0f)
            transform.localRotation = Quat.fromEulerAngles(0f, 90f, 0f)
        }

        val primitives = listOf(
            PrimitiveMeshes.quad,
            PrimitiveMeshes.cube,
            PrimitiveMeshes.sphere,
            PrimitiveMeshes.cylinder,
            PrimitiveMeshes.capsule
        )

        val spacing = 2.5f
        val startX = -((primitives.size - 1) * spacing) / 2f

        for ((index, mesh) in primitives.withIndex()) {
            entity("PrimitiveRow_$index") {
                transform.localPosition = Vec3(startX + (index * spacing), 2.5f, 0f)

                addComponent<MeshRenderer>().also {
                    it.mesh = mesh
                    it.material = Material(Assets.loadDefaultShader()).apply {
                        setTexture("_MainTex", uvTexture, 0)
                    }
                }

                if (index == 2) {
                    targetTransform = this.transform

                    childEntity("OrbitingCube") {
                        addComponent<MeshRenderer>().also {
                            it.mesh = PrimitiveMeshes.cube
                            it.material = Material(Assets.loadDefaultShader()).apply {
                                setTexture("_MainTex", uvTexture, 0)
                            }
                        }
                        transform.localPosition = Vec3(0f, 2f, 0f)
                    }
                }
            }
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