package engine.renderer.bindables

import engine.utils.GLDebug.glCall
import glm_.mat3x3.Mat3
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.opengl.GL11.GL_FALSE
import org.lwjgl.opengl.GL20.*

class Shader(
    vertexSource: String,
    fragmentSource: String
) : Bindable() {

    private val uniformLocations = mutableMapOf<String, Int>()

    init {
        val compiledVertexShader = compileShader(GL_VERTEX_SHADER, vertexSource)
        val compiledFragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentSource)

        gpuID = linkShaders(compiledVertexShader, compiledFragmentShader)

        glCall {
            glDeleteShader(compiledVertexShader)
            glDeleteShader(compiledFragmentShader)
        }
    }

    fun setTexture(name: String, texture: Texture, slot: Int = 0) {
        texture.bind(slot)
        setUniform(name, slot)
    }

    fun setUniform(name: String, value: Int) {
        glCall { glUniform1i(getUniformLocation(name), value) }
    }

    fun setUniform(name: String, value: Float) {
        glCall { glUniform1f(getUniformLocation(name), value) }
    }

    fun setUniform(name: String, value: Vec2) {
        glCall { glUniform2f(getUniformLocation(name), value.x, value.y) }
    }

    fun setUniform(name: String, value: Vec3) {
        glCall { glUniform3f(getUniformLocation(name), value.x, value.y, value.z) }
    }

    fun setUniform(name: String, value: Vec4) {
        glCall { glUniform4f(getUniformLocation(name), value.x, value.y, value.z, value.w) }
    }

    fun setUniform(name: String, value: Mat3) {
        glCall { glUniformMatrix3fv(getUniformLocation(name), false, value.toFloatArray()) }
    }

    fun setUniform(name: String, value: Mat4) {
        glCall { glUniformMatrix4fv(getUniformLocation(name), false, value.toFloatArray()) }
    }

    private fun getUniformLocation(name: String): Int {
        return uniformLocations.getOrPut(name) {
            glGetUniformLocation(gpuID, name)
        }
    }

    private fun compileShader(type: Int, source: String): Int {
        val shaderID = glCreateShader(type)

        if (shaderID == DEFAULT_GPU_ID) {
            error("Failed to create shader.")
        }

        glShaderSource(shaderID, source)
        glCompileShader(shaderID)

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            val log = glGetShaderInfoLog(shaderID)

            glDeleteShader(shaderID)

            error("Shader compile failed:\n$log")
        }

        return shaderID
    }

    private fun linkShaders(
        vertexShader: Int,
        fragmentShader: Int
    ): Int {
        val shaderProgramID = glCreateProgram()

        if (shaderProgramID == 0) {
            error("Failed to create shader program.")
        }

        glAttachShader(shaderProgramID, vertexShader)
        glAttachShader(shaderProgramID, fragmentShader)

        glLinkProgram(shaderProgramID)

        if (glGetProgrami(shaderProgramID, GL_LINK_STATUS) == GL_FALSE) {
            val log = glGetProgramInfoLog(shaderProgramID)

            glDeleteProgram(shaderProgramID)

            error("Shader link failed:\n$log")
        }

        return shaderProgramID
    }

    override fun bind() {
        glCall { glUseProgram(gpuID) }
    }

    override fun unbind() {
        glCall { glUseProgram(DEFAULT_GPU_ID) }
    }

    override fun close() {
        glCall { glDeleteProgram(gpuID) }
    }
}