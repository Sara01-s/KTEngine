package engine.rendering.bindables

import engine.utils.Color
import engine.utils.GLDebug.glCall
import engine.utils.LogLevel
import engine.utils.log
import glm_.mat3x3.Mat3
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.opengl.GL11.GL_FALSE
import org.lwjgl.opengl.GL20.*

class Shader(source: String) : Bindable() {

    companion object {
        private const val VERTEX_SOURCE = "vertex"
        private const val FRAGMENT_SOURCE = "fragment"
    }

    private val uniformLocations = mutableMapOf<String, Int>()
    private val missingUniforms = mutableSetOf<String>()
    private val defines = linkedSetOf<String>()

    private val originalSource = source

    init {
        recompile()
    }

    fun enableDefine(define: String) {
        if (defines.add(define)) {
            recompile()
        }
    }

    fun disableDefine(define: String) {
        if (defines.remove(define)) {
            recompile()
        }
    }

    fun setTexture(name: String, texture: Texture, slot: Int = 0) {
        bind()
        texture.bind(slot)
        setUniform(name, slot)
    }

    // -------------------------
    // Uniforms
    // -------------------------

    fun setUniform(name: String, value: Int) {
        val location = getUniformLocation(name)
        if (location != -1) glCall { glUniform1i(location, value) }
    }

    fun setUniform(name: String, value: Float) {
        val location = getUniformLocation(name)
        if (location != -1) glCall { glUniform1f(location, value) }
    }

    fun setUniform(name: String, value: Vec2) {
        val location = getUniformLocation(name)
        if (location != -1) glCall { glUniform2f(location, value.x, value.y) }
    }

    fun setUniform(name: String, value: Vec3) {
        val location = getUniformLocation(name)
        if (location != -1) glCall { glUniform3f(location, value.x, value.y, value.z) }
    }

    fun setUniform(name: String, value: Vec4) {
        val location = getUniformLocation(name)
        if (location != -1) glCall { glUniform4f(location, value.x, value.y, value.z, value.w) }
    }

    fun setUniform(name: String, value: Color) {
        val location = getUniformLocation(name)
        if (location != -1) glCall { glUniform4f(location, value.r, value.g, value.b, value.a) }
    }

    fun setUniform(name: String, value: Mat3) {
        val location = getUniformLocation(name)
        if (location != -1) glCall { glUniformMatrix3fv(location, false, value.toFloatArray()) }
    }

    fun setUniform(name: String, value: Mat4) {
        val location = getUniformLocation(name)
        if (location != -1) glCall { glUniformMatrix4fv(location, false, value.toFloatArray()) }
    }

    // -------------------------
    // Compile pipeline
    // -------------------------

    private fun recompile() {

        val oldProgram = gpuID

        val shaderSources = parseShader(originalSource)

        val vertexSrc = injectDefines(shaderSources[VERTEX_SOURCE] ?: error("Missing vertex shader"))
        val fragmentSrc = injectDefines(shaderSources[FRAGMENT_SOURCE] ?: error("Missing fragment shader"))

        val vs = compileShader(GL_VERTEX_SHADER, vertexSrc)
        val fs = compileShader(GL_FRAGMENT_SHADER, fragmentSrc)

        val newProgram = linkShaders(vs, fs)

        glCall {
            glDeleteShader(vs)
            glDeleteShader(fs)
        }

        gpuID = newProgram

        if (oldProgram != DEFAULT_GPU_ID) {
            glCall { glDeleteProgram(oldProgram) }
        }

        uniformLocations.clear()
    }

    // -------------------------
    // Defines injection
    // -------------------------

    private fun injectDefines(source: String): String {
        val lines = source.lines()
        if (lines.isEmpty()) return source

        val builder = StringBuilder()

        var i = 0

        if (lines[0].startsWith("#version")) {
            builder.appendLine(lines[0])
            i = 1
        }

        for (define in defines) {
            builder.appendLine("#define $define")
        }

        for (j in i until lines.size) {
            builder.appendLine(lines[j])
        }

        return builder.toString()
    }

    // -------------------------
    // Uniform cache
    // -------------------------

    private fun getUniformLocation(name: String): Int {
        uniformLocations[name]?.let { return it }

        val location = glGetUniformLocation(gpuID, name)
        uniformLocations[name] = location

        if (location == -1) {
            missingUniforms.add(name)
        }

        return location
    }

    // -------------------------
    // Shader parsing
    // -------------------------

    private fun parseShader(source: String): Map<String, String> {
        val shaders = mutableMapOf<String, String>()

        var currentType: String? = null
        val builder = StringBuilder()

        for (line in source.lines()) {

            if (line.startsWith("#type")) {

                if (currentType != null) {
                    shaders[currentType] = builder.toString()
                    builder.clear()
                }

                currentType = line.substringAfter("#type").trim()
            } else {
                builder.appendLine(line)
            }
        }

        if (currentType != null) {
            shaders[currentType] = builder.toString()
        }

        return shaders
    }

    // -------------------------
    // Compile
    // -------------------------

    private fun compileShader(type: Int, source: String): Int {
        val id = glCreateShader(type)

        if (id == 0) error("Failed to create shader")

        glShaderSource(id, source)
        glCompileShader(id)

        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            val logMsg = glGetShaderInfoLog(id)
            glDeleteShader(id)
            error("Shader compile failed:\n$logMsg\n\n$source")
        }

        return id
    }

    private fun linkShaders(vs: Int, fs: Int): Int {
        val program = glCreateProgram()

        if (program == 0) error("Failed to create program")

        glAttachShader(program, vs)
        glAttachShader(program, fs)

        glLinkProgram(program)

        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            val logMsg = glGetProgramInfoLog(program)
            glDeleteProgram(program)
            error("Shader link failed:\n$logMsg")
        }

        return program
    }

    // -------------------------
    // Bindable
    // -------------------------

    override fun bind() {
        glCall { glUseProgram(gpuID) }
    }

    override fun unbind() {
        glCall { glUseProgram(0) }
    }

    override fun close() {
        glCall { glDeleteProgram(gpuID) }
    }
}