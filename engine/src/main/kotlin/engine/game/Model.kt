package engine.game

import engine.components.MeshRenderer
import engine.rendering.bindables.*
import engine.assets.EngineAssets
import engine.utils.Color
import engine.utils.TextureResolver
import engine.utils.decomposeMatrix
import glm_.mat4x4.Mat4
import glm_.quat.Quat
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import org.lwjgl.PointerBuffer
import org.lwjgl.assimp.*
import org.lwjgl.assimp.Assimp.*
import java.lang.AutoCloseable
import java.nio.IntBuffer

class Model(path: String) : AutoCloseable {

    data class MaterialFeatures(
        var hasDiffuse: Boolean = false,
        var hasNormals: Boolean = false,
        var hasSpecular: Boolean = false,
        var hasOpacity: Boolean = false,
        var hasEmissive: Boolean = false,
        var hasRoughness: Boolean = false,
        var hasMetallic: Boolean = false,
        var hasAO: Boolean = false,

        var color: Color = Color.white,

        var specularIntensity: Float = 0.6f,
        var specularPower: Float = 32f
    )

    private val aiScene: AIScene
    private val resourceBaseDir: String

    val meshes = mutableListOf<Mesh>()

    init {
        val flags =
            aiProcess_Triangulate or
            aiProcess_JoinIdenticalVertices or
            aiProcess_CalcTangentSpace or
            aiProcess_GenSmoothNormals or
            aiProcess_ImproveCacheLocality or
            aiProcess_LimitBoneWeights or
            aiProcess_FindInvalidData or
            aiProcess_FlipUVs

        val sanitizedPath = path.replace('\\', '/')

        resourceBaseDir = sanitizedPath.substringBeforeLast('/', "")

        aiScene = aiImportFile(path, flags)
            ?: error(aiGetErrorString() ?: "Failed to load model")

        val aiMaterials = aiScene.mMaterials()

        for (i in 0 until aiScene.mNumMeshes()) {
            val aiMesh = AIMesh.create(aiScene.mMeshes()!![i])
            meshes += parseMesh(aiMesh, aiMaterials)
        }
    }

    fun instantiate(parentEntity: Entity) {
        attachNodeToEntity(aiScene.mRootNode()!!, parentEntity)
    }

    private fun attachNodeToEntity(aiNode: AINode, entity: Entity) {
        val meshIndices = aiNode.mMeshes()

        if (meshIndices != null) {
            val numMeshes = aiNode.mNumMeshes()

            for (i in 0 until numMeshes) {
                val mesh = meshes[meshIndices[i]]

                if (numMeshes == 1) {
                    val renderer = entity.addComponent<MeshRenderer>()

                    renderer.mesh = mesh
                    renderer.material = mesh.material

                } else {
                    entity.entity("${aiNode.mName().dataString()}_mesh_$i") {
                        val renderer = addComponent<MeshRenderer>()

                        renderer.mesh = mesh
                        renderer.material = mesh.material
                    }
                }
            }
        }

        val children = aiNode.mChildren() ?: return

        for (i in 0 until aiNode.mNumChildren()) {
            val childNode = AINode.create(children[i])
            val transformMatrix = assimpMatrixToMat4(childNode.mTransformation())

            entity.entity(childNode.mName().dataString()) {
                val position = Vec3()
                val rotation = Quat()
                val scale = Vec3()

                decomposeMatrix(transformMatrix, position, rotation, scale)

                transform.localPosition = position
                transform.localRotation = rotation
                transform.localScale = scale

                attachNodeToEntity(childNode, this)
            }
        }
    }

    private fun parseMesh(aiMesh: AIMesh, aiMaterials: PointerBuffer?): Mesh {
        val (layout, vertices) = parseLayoutAndVertices(aiMesh)
        val indices = parseIndices(aiMesh)
        val features = parseMaterialFeatures(aiMesh, aiMaterials)
        val shader = createShaderFromFeatures(features)
        val material = Material(shader)

        material.setColor4("_Color", features.color)
        material.setFloat("_SpecularIntensity", features.specularIntensity)
        material.setFloat("_SpecularPower", features.specularPower)

        if (aiMaterials != null)
            bindTextures(material, aiMesh, aiMaterials, features)

        return Mesh(layout, vertices, indices, material)
    }

    private fun parseLayoutAndVertices(aiMesh: AIMesh): Pair<VertexLayout, List<Any>> {
        val vertices = mutableListOf<Any>()

        val positions = aiMesh.mVertices() ?: error("Mesh has no vertices")
        val normals = aiMesh.mNormals()
        val texCoords = aiMesh.mTextureCoords(0)
        val tangents = aiMesh.mTangents()
        val bitangents = aiMesh.mBitangents()

        val hasNormals = normals != null
        val hasTexCoords = texCoords != null
        val hasTangents = tangents != null && bitangents != null

        val layout = VertexLayout()
            .append(VertexLayout.ElementType.Position3D)

        if (hasNormals)
            layout.append(VertexLayout.ElementType.Normal3D)

        if (hasTexCoords)
            layout.append(VertexLayout.ElementType.Texture2D)

        if (hasTangents) {
            layout.append(VertexLayout.ElementType.Tangent3D)
            layout.append(VertexLayout.ElementType.Bitangent3D)
        }

        for (i in 0 until aiMesh.mNumVertices()) {
            val pos = positions[i]

            vertices += Vec3(pos.x(), pos.y(), pos.z())

            if (hasNormals) {
                val n = normals[i]
                vertices += Vec3(n.x(), n.y(), n.z())
            }

            if (hasTexCoords) {
                val uv = texCoords[i]
                vertices += Vec2(uv.x(), uv.y())
            }

            if (hasTangents) {
                val t = tangents[i]
                val b = bitangents[i]

                vertices += Vec3(t.x(), t.y(), t.z())
                vertices += Vec3(b.x(), b.y(), b.z())
            }
        }

        return Pair(layout, vertices)
    }

    private fun parseIndices(aiMesh: AIMesh): IntArray {
        val indices = ArrayList<Int>(aiMesh.mNumFaces() * 3)

        for (i in 0 until aiMesh.mNumFaces()) {
            val face = aiMesh.mFaces()[i]

            indices += face.mIndices()[0]
            indices += face.mIndices()[1]
            indices += face.mIndices()[2]
        }

        return indices.toIntArray()
    }

    private fun parseMaterialFeatures(aiMesh: AIMesh, aiMaterials: PointerBuffer?): MaterialFeatures {
        val features = MaterialFeatures()

        if (aiMaterials == null)
            return features

        val material = AIMaterial.create(aiMaterials.get(aiMesh.mMaterialIndex()))
        val path = AIString.calloc()

        try {
            features.hasDiffuse =  hasTexture(material, aiTextureType_BASE_COLOR, path) || hasTexture(material, aiTextureType_DIFFUSE, path)
            features.hasSpecular = hasTexture(material, aiTextureType_SPECULAR, path)
            features.hasNormals =  hasTexture(material, aiTextureType_NORMALS, path) || hasTexture(material, aiTextureType_HEIGHT, path)
            features.hasOpacity =  hasTexture(material, aiTextureType_OPACITY, path) || hasTexture(material, aiTextureType_DISPLACEMENT, path)
            features.hasEmissive = hasTexture(material, aiTextureType_EMISSIVE, path)
            features.hasRoughness = hasTexture(material, aiTextureType_DIFFUSE_ROUGHNESS, path)
            features.hasMetallic = hasTexture(material, aiTextureType_METALNESS, path) || hasTexture(material, aiTextureType_UNKNOWN, path)
            features.hasAO = hasTexture(material, aiTextureType_AMBIENT_OCCLUSION, path) ||
                    hasTexture(material, aiTextureType_LIGHTMAP, path) ||
                    hasTexture(material, aiTextureType_UNKNOWN, path) ||
                    hasTexture(material, aiTextureType_SHININESS, path)

            val color = AIColor4D.create()

            if (aiGetMaterialColor(material, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color) == aiReturn_SUCCESS) {
                features.color = Color(color.r(), color.g(), color.b(), color.a())
            }

        } finally {
            path.free()
        }

        return features
    }

    private fun hasTexture(material: AIMaterial, type: Int, path: AIString): Boolean {
        return aiGetMaterialTexture(
            /* pMat = */ material,
            /* type = */ type,
            /* index = */ 0,
            /* path = */ path,
            /* mapping = */ null as IntBuffer?,
            /* uvindex = */ null,
            /* blend = */ null,
            /* op = */ null,
            /* mapmode = */ null,
            /* flags = */ null
        ) == aiReturn_SUCCESS
    }

    private fun createShaderFromFeatures(features: MaterialFeatures): Shader {
        val shaderPath =
            if (features.hasNormals || features.hasDiffuse)
                "engine_assets/shaders/shd_lit.glsl"
            else
                "engine_assets/shaders/shd_unlit.glsl"

        val shader = Shader(EngineAssets.loadText(shaderPath))

        if (features.hasDiffuse) shader.enableDefine("HAS_DIFFUSE")
        if (features.hasNormals) shader.enableDefine("HAS_TANGENTS")
        if (features.hasSpecular) shader.enableDefine("HAS_SPECULAR")
        if (features.hasOpacity) shader.enableDefine("HAS_OPACITY")
        if (features.hasEmissive) shader.enableDefine("HAS_EMISSIVE")
        if (features.hasRoughness) shader.enableDefine("HAS_ROUGHNESS")
        if (features.hasMetallic) shader.enableDefine("HAS_METALLIC")
        if (features.hasAO) shader.enableDefine("HAS_AO")

        return shader
    }

    private fun bindTextures(materialObj: Material, aiMesh: AIMesh, materials: PointerBuffer, features: MaterialFeatures) {
        val material = AIMaterial.create(materials.get(aiMesh.mMaterialIndex()))
        val path = AIString.calloc()

        try {
            fun tryBind(uniformName: String, slot: Int, vararg types: Int) {
                for (type in types) {
                    val result = aiGetMaterialTexture(
                        material, type, 0, path,
                        null as IntBuffer?, null, null, null, null, null
                    )

                    if (result == aiReturn_SUCCESS) {
                        val texture = TextureResolver.resolveTexture(path.dataString(), resourceBaseDir, aiScene)
                        if (texture != null) {
                            materialObj.setTexture(uniformName, texture, slot)
                            return
                        }
                    }
                }
            }

            if (features.hasDiffuse) {
                tryBind("_DiffuseTexture", 0, aiTextureType_BASE_COLOR, aiTextureType_DIFFUSE)
            }

            if (features.hasSpecular) {
                tryBind("_SpecularTexture", 1, aiTextureType_SPECULAR, aiTextureType_SHININESS)
            }

            if (features.hasNormals) {
                tryBind("_NormalTexture", 2, aiTextureType_NORMALS, aiTextureType_HEIGHT)
            }

            if (features.hasOpacity) {
                tryBind("_OpacityTexture", 3, aiTextureType_OPACITY, aiTextureType_DISPLACEMENT)
            }

            if (features.hasEmissive) {
                tryBind("_EmissiveTexture", 4, aiTextureType_EMISSIVE)
            }

            if (features.hasRoughness) {
                tryBind("_RoughnessTexture", 5, aiTextureType_DIFFUSE_ROUGHNESS)
            }

            if (features.hasMetallic) {
                tryBind("_MetallicTexture", 6, aiTextureType_METALNESS, aiTextureType_UNKNOWN)
            }

            if (features.hasAO) {
                tryBind("_AmbientOcclusionTexture", 7, aiTextureType_AMBIENT_OCCLUSION, aiTextureType_LIGHTMAP, aiTextureType_UNKNOWN, aiTextureType_SHININESS)
            }

        } finally {
            path.free()
        }
    }

    private fun assimpMatrixToMat4(m: AIMatrix4x4): Mat4 {
        return Mat4(
            m.a1(), m.b1(), m.c1(), m.d1(),
            m.a2(), m.b2(), m.c2(), m.d2(),
            m.a3(), m.b3(), m.c3(), m.d3(),
            m.a4(), m.b4(), m.c4(), m.d4()
        )
    }

    override fun close() {
        meshes.forEach { it.close() }
        meshes.clear()
    }
}