package engine.utils

import engine.game.Model
import engine.rendering.bindables.Mesh
import engine.systems.Assets

object PrimitiveMeshes : AutoCloseable {
    private val quadModel:     Model = Assets.loadModel("/models/primitives/model_quad.obj")
    private val cubeModel:     Model = Assets.loadModel("/models/primitives/model_cube.obj")
    private val sphereModel:   Model = Assets.loadModel("/models/primitives/model_sphere.obj")
    private val planeModel:    Model = Assets.loadModel("/models/primitives/model_plane.obj")
    private val cylinderModel: Model = Assets.loadModel("/models/primitives/model_cylinder.obj")
    private val capsuleModel:  Model = Assets.loadModel("/models/primitives/model_capsule.obj")

    val quad:     Mesh = quadModel.meshes[0]
    val cube:     Mesh = cubeModel.meshes[0]
    val sphere:   Mesh = sphereModel.meshes[0]
    val plane:    Mesh = planeModel.meshes[0]
    val cylinder: Mesh = cylinderModel.meshes[0]
    val capsule:  Mesh = capsuleModel.meshes[0]

    override fun close() {
        quadModel.close()
        cubeModel.close()
        sphereModel.close()
        planeModel.close()
        cylinderModel.close()
        capsuleModel.close()
    }
}