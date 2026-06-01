package engine.rendering.bindables

import engine.assets.Assets
import engine.utils.Color
import glm_.mat3x3.Mat3
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.opengl.GL13.*

class Material(val shader: Shader) : Bindable() {
    private data class TextureSlot(
        val texture: Texture? = null,
        val gpuID: Int? = null,
        val gpuTarget: Int = GL_TEXTURE_2D,
        val explicitSlot: Int?
    )

    private val ints     = mutableMapOf<String, Int>()
    private val floats   = mutableMapOf<String, Float>()
    private val vec3s    = mutableMapOf<String, Vec3>()
    private val vec4s    = mutableMapOf<String, Vec4>()
    private val mat3s    = mutableMapOf<String, Mat3>()
    private val mat4s    = mutableMapOf<String, Mat4>()
    private val textures = mutableMapOf<String, TextureSlot>()

    init {
        setColor4("_Color", Color.white)
    }

    fun setInt(name: String, value: Int)         { ints[name]     = value }
    fun setFloat(name: String, value: Float)     { floats[name]   = value }
    fun setVec3(name: String, value: Vec3)       { vec3s[name]    = value }
    fun setVec4(name: String, value: Vec4)       { vec4s[name]    = value }
    fun setMat3(name: String, value: Mat3)       { mat3s[name]    = value }
    fun setMat4(name: String, value: Mat4)       { mat4s[name]    = value }

    fun setTexture(name: String, value: Texture) { textures[name] = TextureSlot(texture = value, explicitSlot = null) }
    fun setTexture(name: String, value: Texture, slot: Int) { textures[name] = TextureSlot(texture = value, explicitSlot = slot) }
    fun setTexture(name: String, gpuID: Int, slot: Int) { textures[name] = TextureSlot(texture = null, gpuID = gpuID, explicitSlot = slot) }
        fun setTexture(name: String, gpuID: Int, slot: Int, target: Int = GL_TEXTURE_2D) {
        textures[name] = TextureSlot(gpuID = gpuID, gpuTarget = target, explicitSlot = slot)
    }

    fun setColor3(name: String, color: Color) { setVec3(name, Vec3(color.r, color.g, color.b)) }
    fun setColor4(name: String, color: Color) { setVec4(name, Vec4(color.r, color.g, color.b, color.a)) }

    fun setMainColor4(color: Color)              { setColor4("_Color", color) }
    fun setMainTexture(texture: Texture)         { setTexture("_MainTex", texture, 0) }

    override fun bind() {
        shader.bind()

        for ((name, value) in ints)   if (shader.hasUniform(name)) shader.setUniform(name, value)
        for ((name, value) in floats) if (shader.hasUniform(name)) shader.setUniform(name, value)
        for ((name, value) in vec3s)  if (shader.hasUniform(name)) shader.setUniform(name, value)
        for ((name, value) in vec4s)  if (shader.hasUniform(name)) shader.setUniform(name, value)
        for ((name, value) in mat3s)  if (shader.hasUniform(name)) shader.setUniform(name, value)
        for ((name, value) in mat4s)  if (shader.hasUniform(name)) shader.setUniform(name, value)

        var autoSlot = 0
        if (textures.isEmpty()) {
            if (shader.hasUniform("_MainTex")) {
                shader.setUniform("_MainTex", Assets.loadWhiteTexture(), slot = 0)
            }
        }
        else {
            for ((name, slotData) in textures) {
                if (shader.hasUniform(name)) {
                    val targetSlot = slotData.explicitSlot ?: autoSlot++

                    if (slotData.texture != null) {
                        shader.setUniform(name, slotData.texture, targetSlot)
                    }
                    else if (slotData.gpuID != null) {
                        glActiveTexture(GL_TEXTURE0 + targetSlot)
                        glBindTexture(slotData.gpuTarget, slotData.gpuID)
                        shader.setUniform(name, targetSlot)
                    }

                    if (targetSlot >= autoSlot) {
                        autoSlot = targetSlot + 1
                    }
                }
            }
        }
    }

    fun clone(): Material {
        val newMat = Material(this.shader)

        newMat.ints.putAll(this.ints)
        newMat.floats.putAll(this.floats)
        newMat.vec3s.putAll(this.vec3s)
        newMat.vec4s.putAll(this.vec4s)
        newMat.mat3s.putAll(this.mat3s)
        newMat.mat4s.putAll(this.mat4s)
        newMat.textures.putAll(this.textures)

        return newMat
    }

    override fun unbind() {
        shader.unbind()
    }

    override fun close() {}
}