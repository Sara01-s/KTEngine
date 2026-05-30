package engine.rendering.bindables

import engine.assets.Assets
import engine.utils.Color
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec4.Vec4

class Material(val shader: Shader) : Bindable() {
    private data class TextureSlot(val texture: Texture, val explicitSlot: Int?)

    private val ints     = mutableMapOf<String, Int>()
    private val floats   = mutableMapOf<String, Float>()
    private val vec3s    = mutableMapOf<String, Vec3>()
    private val vec4s    = mutableMapOf<String, Vec4>()
    private val mat4s    = mutableMapOf<String, Mat4>()
    private val textures = mutableMapOf<String, TextureSlot>()

    init {
        setColor("_Color", Color.white)
    }

    fun setInt(name: String, value: Int)         { ints[name]     = value }
    fun setFloat(name: String, value: Float)     { floats[name]   = value }
    fun setVec3(name: String, value: Vec3)       { vec3s[name]    = value }
    fun setVec4(name: String, value: Vec4)       { vec4s[name]    = value }
    fun setMat4(name: String, value: Mat4)       { mat4s[name]    = value }
    fun setColor(name: String, value: Color)     { setVec4(name, Vec4(value.r, value.g, value.b, value.a)) }
    fun setColor(color: Color)                   { setColor("_Color", color) }
    fun setTexture(name: String, value: Texture) { textures[name] = TextureSlot(value, null) }
    fun setMainTexture(texture: Texture)         { setTexture("_MainTex", texture, 0) }
    fun setTexture(name: String, value: Texture, slot: Int) { textures[name] = TextureSlot(value, slot) }

    fun getInt(name: String): Int?         = ints[name]
    fun getFloat(name: String): Float?     = floats[name]
    fun getVec3(name: String): Vec3?       = vec3s[name]
    fun getVec4(name: String): Vec4?       = vec4s[name]
    fun getMat4(name: String): Mat4?       = mat4s[name]
    fun getTexture(name: String): Texture? = textures[name]?.texture
    fun getColor(name: String): Color?     = vec4s[name]?.let { Color(it.x, it.y, it.z, it.w) }

    override fun bind() {
        shader.bind()

        for ((name, value) in ints)   shader.setUniform(name, value)
        for ((name, value) in floats) shader.setUniform(name, value)
        for ((name, value) in vec3s)  shader.setUniform(name, value)
        for ((name, value) in vec4s)  shader.setUniform(name, value)
        for ((name, value) in mat4s)  shader.setUniform(name, value)

        var autoSlot = 0

        if (textures.isEmpty()) {
            shader.setTexture("_MainTex", Assets.loadWhiteTexture(), slot =  0)
        }
        else {
            for ((name, textureSlot) in textures) {
                val targetSlot = textureSlot.explicitSlot ?: autoSlot++
                shader.setTexture(name, textureSlot.texture, targetSlot)

                if (targetSlot >= autoSlot) {
                    autoSlot = targetSlot + 1
                }
            }
        }
    }

    override fun unbind() {
        shader.unbind()
    }

    override fun close() {}
}