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

class Scene3 : Scene() {
    private val cameraEntity by entityRef("MainCamera")
    private val cubeEntity by entityRef("Cube")

    private var targetTransform: Transform? = null
    private val rotationSpeed = 1.0f

    override fun start() {
        Input.Mouse.captured = true
        val uvTexture = Assets.loadTexture("/textures/tex_test_uv.png")

        entity("MainCamera") {
            addComponent<Camera>()
            addComponent<FirstPersonController>()
            transform.localPosition = Vec3(0f, 6f, -18f)
        }

        entity("GridFloor") {
            addComponent<MeshRenderer>().also {
                it.mesh = PrimitiveMeshes.plane
                it.material = Material(Assets.loadShader("/shaders/shd_grid.glsl"))
            }
            transform.localScale = Vec3(1000f, 1f, 1000f)
        }

        val cube = entity("Cube") {
            addComponent<MeshRenderer>().also {
                it.mesh = PrimitiveMeshes.cube
                it.material = Material(Assets.loadDefaultShader()).apply {
                    setMainTexture(uvTexture)
                }
            }
            transform.localScale = Vec3(7f)
            transform.localPosition = Vec3(0f, 5f, 0f)
            transform.localRotation = Quat.fromEulerAngles(45f, 45f, 45f)
        }

        val primitives = mapOf(
            "Quad"     to PrimitiveMeshes.quad,
            "Sphere"   to PrimitiveMeshes.sphere,
            "Plane"    to PrimitiveMeshes.plane,
            "Cylinder" to PrimitiveMeshes.cylinder,
            "Capsule"  to PrimitiveMeshes.capsule
        )

        var offset = -25f
        for ((name, mesh) in primitives) {
            entity(name) {
                addComponent<MeshRenderer>().also {
                    it.mesh = mesh
                    it.material = Material(Assets.loadDefaultShader()).apply {
                        setMainTexture(uvTexture)
                    }
                }
                transform.localScale = Vec3(4f)
                transform.localPosition = Vec3(offset, 5f, 0f)
            }
            offset += 12f

            if (offset in -5f..5f) offset += 10f
        }

        val cube2 = entity("Cube2") {
            addComponent<MeshRenderer>().also {
                it.mesh = PrimitiveMeshes.cube
                it.material = Material(Assets.loadDefaultShader()).apply {
                    setMainTexture(uvTexture)
                }
            }
            transform.localScale = Vec3(2f)
            transform.localPosition = Vec3(10f, 3f, 0f)
        }

        cube.transform.addChild(cube2.transform)
    }

    override fun update() {
        cameraEntity.getComponent<FirstPersonController>().update()

        val angle = Time.time * 25f
        cubeEntity.transform.localRotation = Quat.fromEulerAngles(angle, -angle, angle)

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