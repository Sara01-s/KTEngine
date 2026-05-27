package engine.scenes

import engine.components.Camera
import engine.components.MeshRenderer
import engine.game.Time
import engine.rendering.bindables.Material
import engine.systems.Assets
import engine.systems.Input
import engine.systems.Key
import engine.systems.Player
import engine.systems.Axis
import engine.utils.PrimitiveMeshes
import engine.utils.clamp
import engine.utils.fromEulerAngles
import glm_.quat.Quat
import glm_.vec3.Vec3

class Scene3D : Scene() {
    private val moveSpeed = 12f
    private val lookSensitivity = 0.15f
    private val rotationSpeed = 1f

    private var cameraPitch = 0f
    private var cameraYaw = 0f

    private val camera by entityRef("MainCamera")
    private val orbitingCube by entityRef("OrbitingCube")

    private val rotatingTransforms = mutableListOf<engine.components.Transform>()

    init {
        Input.Mouse.captured = true

        val defaultShader = Assets.loadDefaultShader()
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

        val spacing = 4.5f
        val startX = -((primitives.size - 1) * spacing) / 2f

        primitives.forEachIndexed { index, mesh ->
            entity("PrimitiveRow_$index") {
                transform.localPosition = Vec3(startX + (index * spacing), 2.5f, 0f)
                transform.localScale = Vec3(2f)

                addComponent<MeshRenderer>().also {
                    it.mesh = mesh
                    it.material = Material(defaultShader).apply {
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
        val dt = Time.deltaTime

        if (Input.Keyboard.isJustPressed(Key.Escape)) Input.Mouse.captured = false
        if (Input.Keyboard.isJustPressed(Key.Enter))  Input.Mouse.captured = true

        val angleDelta = rotationSpeed * dt
        rotatingTransforms.forEach { transform ->
            transform.rotate(pitch = angleDelta * 0.5f, yaw = angleDelta, roll = 0f)
        }

        orbitingCube.transform.rotate(pitch = 0f, yaw = -rotationSpeed * 2f * dt, roll = 0f)

        if (Input.Mouse.captured) {
            val mouseDelta = Input.Mouse.delta
            cameraYaw += mouseDelta.x * lookSensitivity
            cameraPitch += mouseDelta.y * lookSensitivity
            cameraPitch = clamp(cameraPitch, -89f, 89f)

            camera.transform.localRotation = Quat.fromEulerAngles(cameraPitch, cameraYaw, 0f)
        }

        val moveDirection = Vec3(0f, 0f, 0f)
        val horizontalAxis = Input.getAxis(Player.P1, Axis.Horizontal)
        val verticalAxis = Input.getAxis(Player.P1, Axis.Vertical)

        if (horizontalAxis != 0f) moveDirection.plusAssign(camera.transform.right * horizontalAxis)
        if (verticalAxis != 0f)   moveDirection.plusAssign(camera.transform.forward * verticalAxis)

        if (Input.Keyboard.isPressed(Key.Space))     moveDirection.plusAssign(Vec3(0f, 1f, 0f))
        if (Input.Keyboard.isPressed(Key.LeftShift)) moveDirection.minusAssign(Vec3(0f, 1f, 0f))

        if (moveDirection.length2() > 0f) {
            moveDirection.normalizeAssign()
        }

        camera.transform.localPosition = camera.transform.localPosition + (moveDirection * moveSpeed * dt)
    }
}