package engine.renderer.bindables

import engine.utils.GLDebug.glCall
import glm_.mat3x3.Mat3
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.opengl.GL11.GL_FALSE
import org.lwjgl.opengl.GL20.GL_COMPILE_STATUS
import org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL20.GL_LINK_STATUS
import org.lwjgl.opengl.GL20.GL_VERTEX_SHADER
import org.lwjgl.opengl.GL20.glAttachShader
import org.lwjgl.opengl.GL20.glCompileShader
import org.lwjgl.opengl.GL20.glCreateProgram
import org.lwjgl.opengl.GL20.glCreateShader
import org.lwjgl.opengl.GL20.glDeleteProgram
import org.lwjgl.opengl.GL20.glGetProgramInfoLog
import org.lwjgl.opengl.GL20.glGetProgrami
import org.lwjgl.opengl.GL20.glGetShaderInfoLog
import org.lwjgl.opengl.GL20.glGetShaderi
import org.lwjgl.opengl.GL20.glGetUniformLocation
import org.lwjgl.opengl.GL20.glLinkProgram
import org.lwjgl.opengl.GL20.glShaderSource
import org.lwjgl.opengl.GL20.glUniform1f
import org.lwjgl.opengl.GL20.glUniform1i
import org.lwjgl.opengl.GL20.glUniform2f
import org.lwjgl.opengl.GL20.glUniform3f
import org.lwjgl.opengl.GL20.glUniform4f
import org.lwjgl.opengl.GL20.glUniformMatrix3fv
import org.lwjgl.opengl.GL20.glUniformMatrix4fv
import org.lwjgl.opengl.GL20.glUseProgram

class Shader(vertexShaderFilePath: String, fragmentShaderFilePath: String) : Bindable() {

    private val uniformLocations = mutableMapOf<String, Int>()

    init {
        val vertexShaderSource =
            object {}.javaClass.getResource(vertexShaderFilePath)?.readText()
                ?: error("Vertex shader not found: $vertexShaderFilePath")

        val fragmentShaderSource =
            object {}.javaClass.getResource(fragmentShaderFilePath)?.readText()
                ?: error("Fragment shader not found: $fragmentShaderFilePath")

        val compiledVertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource)
        val compiledFragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource)

        gpuID = linkShaders(compiledVertexShader, compiledFragmentShader)

        bind()
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

        if (shaderID == 0) {
            error("Failed to create shader (glCreateShader returned 0)")
        }

        glShaderSource(shaderID, source)
        glCompileShader(shaderID)

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            val log = glGetShaderInfoLog(shaderID)
            error("Shader compile failed:\n$log")
        }

        return shaderID
    }

    private fun linkShaders(vertexShader: Int, fragmentShader: Int): Int {
        var shaderProgramID: Int = DEFAULT_GPU_ID

        glCall {
            shaderProgramID = glCreateProgram()

            glAttachShader(shaderProgramID, vertexShader)
            glAttachShader(shaderProgramID, fragmentShader)

            glLinkProgram(shaderProgramID)

            if (glGetProgrami(shaderProgramID, GL_LINK_STATUS) == GL_FALSE) {
                val log = glGetProgramInfoLog(shaderProgramID)

                glDeleteProgram(shaderProgramID)

                error("Shader link failed:\n$log")
            }
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