package engine.game

import engine.components.Transform
import engine.rendering.bindables.Mesh
import glm_.mat4x4.Mat4
import org.lwjgl.assimp.AIMaterial
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.Assimp.*
import org.lwjgl.assimp.AIScene
import org.lwjgl.assimp.AINode
import java.lang.AutoCloseable

class ModelNode(val meshes: List<Mesh>, val transform: Transform, val name: String = "New Node") {
    val children = mutableListOf<ModelNode>()
    var appliedTransform: Transform? = null

    fun draw(accumulatedTransform: Transform) {

    }

    fun addChild(child: ModelNode) {
        children.add(child)
    }
}

class Model(path: String) : AutoCloseable {
    private val aiScene: AIScene?
    private val meshes = mutableListOf<Mesh>()

    init {
        val flags =
            aiProcess_Triangulate or
            aiProcess_FlipUVs or
            aiProcess_JoinIdenticalVertices or
            aiProcess_OptimizeMeshes

        aiScene = aiImportFile(path, flags)

        if (aiScene == null || aiScene.mRootNode() == null) {
            error("Assimp Error: Could not load model from [$path]. Error: ${aiGetErrorString()}")
        }

        parseNode(aiScene.mRootNode()!!)
    }

    fun draw() {
        // Aquí iniciarás la llamada al dibujo del nodo raíz
    }

    fun getModelMatrix(): Mat4 {
        return Mat4()
    }

    private fun parseMesh(aiMesh: AIMesh, aiMaterial: AIMaterial, path: String) : Mesh? {
        return null
    }

    private fun parseNode(aiNode: AINode) : ModelNode? {
        val nodeName = aiNode.mName().dataString()

        val numChildren = aiNode.mNumChildren()
        val childrenBuffer = aiNode.mChildren()

        if (childrenBuffer != null) {
            for (i in 0 until numChildren) {
                val aiChildNode = AINode.create(childrenBuffer.get(i))
                parseNode(aiChildNode)
            }
        }

        return  null
    }

    override fun close() {
        if (aiScene != null) {
            aiReleaseImport(aiScene)
        }
    }
}