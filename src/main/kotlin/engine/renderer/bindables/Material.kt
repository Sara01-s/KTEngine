package engine.renderer.bindables

import engine.renderer.Color
import glm_.mat4x4.Mat4
import glm_.vec4.Vec4

class Material(val shader: Shader) : Bindable() {

    private val ints     = mutableMapOf<String, Int>()
    private val floats   = mutableMapOf<String, Float>()
    private val vec4s    = mutableMapOf<String, Vec4>()
    private val mat4s    = mutableMapOf<String, Mat4>()
    private val textures = mutableMapOf<String, Texture>()

    fun setInt(name: String, value: Int)         { ints[name]     = value }
    fun setFloat(name: String, value: Float)     { floats[name]   = value }
    fun setVec4(name: String, value: Vec4)       { vec4s[name]    = value }
    fun setMat4(name: String, value: Mat4)       { mat4s[name]    = value }
    fun setTexture(name: String, value: Texture) { textures[name] = value }
    fun setColor(name: String, value: Color) {
        setVec4(name, Vec4(value.r, value.g, value.b, value.a))
    }

    fun getInt(name: String): Int?         = ints[name]
    fun getFloat(name: String): Float?     = floats[name]
    fun getVec4(name: String): Vec4?       = vec4s[name]
    fun getMat4(name: String): Mat4?       = mat4s[name]
    fun getTexture(name: String): Texture? = textures[name]
    fun getColor(name: String): Color? = vec4s[name]?.let { Color(it.x, it.y, it.z, it.w) }

    override fun bind() {
        shader.bind()

        for ((name, value) in ints)   shader.setUniform(name, value)
        for ((name, value) in floats) shader.setUniform(name, value)
        for ((name, value) in vec4s)  shader.setUniform(name, value)
        for ((name, value) in mat4s)  shader.setUniform(name, value)

        var slot = 0

        for ((name, texture) in textures) {
            shader.setTexture(name, texture, slot++)
        }
    }

    override fun unbind() {
        shader.unbind()
    }

    override fun close() {}
}