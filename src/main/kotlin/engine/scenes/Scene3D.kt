package engine.scenes

import engine.components.MeshRenderer
import engine.game.Camera
import engine.utils.PrimitiveMeshes
import glm_.vec3.Vec3

class Scene3D : Scene() {
    val cube = createEntity().apply {
        addComponent<MeshRenderer>().mesh = PrimitiveMeshes.cube
    }

    init {
        Camera.position = Vec3(0f, 0f, 10f)
    }
}