package engine.utils

import engine.rendering.bindables.Material
import engine.rendering.bindables.Texture
import org.lwjgl.assimp.AIMaterial
import org.lwjgl.assimp.AIScene
import org.lwjgl.assimp.AIString
import org.lwjgl.assimp.AITexture
import org.lwjgl.assimp.Assimp.aiGetMaterialTexture
import org.lwjgl.assimp.Assimp.aiReturn_SUCCESS
import java.nio.ByteBuffer
import java.nio.IntBuffer

object TextureResolver {
    fun resolveTexture(rawPath: String, resourceBaseDir: String, aiScene: AIScene?): Texture? {
        if (rawPath.startsWith("*")) {
            return loadEmbedded(rawPath, aiScene)
        }

        val resolvedPath = resolvePath(rawPath.substringAfterLast('/'), resourceBaseDir)
        return resolvedPath?.let { Texture(it) }
    }

    fun resolvePath(fileName: String, resourceBaseDir: String): String? {
        val candidates = mutableListOf<String>()
        val relativePath = fileName.removePrefix("./")

        if (resourceBaseDir.isNotEmpty()) {
            candidates += "$resourceBaseDir/$relativePath"
            candidates += "$resourceBaseDir/$fileName"
            candidates += "$resourceBaseDir/../textures/$fileName"
        }

        candidates += "/textures/$fileName"
        candidates += "/models/textures/$fileName"
        candidates += "/$fileName"
        candidates += relativePath

        for (candidate in candidates) {
            val cleanPath = if (candidate.startsWith("/")) candidate else "/$candidate"
            val stream = javaClass.getResourceAsStream(cleanPath)
            if (stream != null) {
                stream.close()
                return cleanPath
            }
        }

        return null
    }

    private fun loadEmbedded(rawPath: String, aiScene: AIScene?): Texture? {
        if (aiScene == null) return null

        val textureIndex = rawPath.removePrefix("*").toIntOrNull() ?: return null
        val textures = aiScene.mTextures() ?: return null
        if (textureIndex >= aiScene.mNumTextures()) return null

        val aiTexture = AITexture.create(textures[textureIndex])

        return if (aiTexture.mHeight() == 0) {
            val compressedBuffer = aiTexture.pcDataCompressed() ?: return null
            val compressedSize = aiTexture.mWidth()
            val byteBuffer = ByteBuffer.allocateDirect(compressedSize)

            for (i in 0 until compressedSize) {
                byteBuffer.put(compressedBuffer[i])
            }

            byteBuffer.flip()
            Texture.fromMemory(byteBuffer)
        } else {
            Texture.fromAssimpRGBA(aiTexture.mWidth(), aiTexture.mHeight(), aiTexture.pcData() ?: return null)
        }
    }
}