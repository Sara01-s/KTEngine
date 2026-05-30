package engine.rendering.bindables

import engine.Application
import engine.assets.AssetUtils
import engine.utils.Color
import glm_.mat3x3.Mat3
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.opengl.GL11.GL_FALSE
import org.lwjgl.opengl.GL20.*
import java.nio.file.Files
import java.nio.file.Files.readString

class Shader(private val source: String, private val shaderPath: String = "") : Bindable() {

    companion object {
        private const val VERTEX_SOURCE = "vertex"
        private const val FRAGMENT_SOURCE = "fragment"
    }

    private val uniformLocations = mutableMapOf<String, Int>()
    private val defines = linkedSetOf<String>()

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

    private fun recompile() {
        val oldProgram = gpuID
        val shaderSources = parseShader(source)
        val baseDir = shaderPath.substringBeforeLast("/", "")

        val vertexSrc = injectDefines(resolveIncludes(shaderSources[VERTEX_SOURCE] ?: error("Missing vertex shader"), baseDir))
        val fragmentSrc = injectDefines(resolveIncludes(shaderSources[FRAGMENT_SOURCE] ?: error("Missing fragment shader"), baseDir))

        val vs = compileShader(GL_VERTEX_SHADER, vertexSrc)
        val fs = compileShader(GL_FRAGMENT_SHADER, fragmentSrc)

        uniformLocations.clear()

        gpuID = linkShaders(vs, fs)

        glDeleteShader(vs)
        glDeleteShader(fs)

        if (oldProgram != 0) {
            glDeleteProgram(oldProgram)
        }
    }

    fun hasUniform(name: String): Boolean {
        return getUniformLocation(name) != -1
    }

    private fun getUniformLocation(name: String): Int {
        return uniformLocations.getOrPut(name) {
            glGetUniformLocation(gpuID, name)
        }
    }

    fun setUniform(name: String, value: Int) {
        if (hasUniform(name)) glUniform1i(getUniformLocation(name), value)
    }

    fun setUniform(name: String, value: Float) {
        if (hasUniform(name)) glUniform1f(getUniformLocation(name), value)
    }

    fun setUniform(name: String, value: Vec2) {
        if (hasUniform(name)) glUniform2f(getUniformLocation(name), value.x, value.y)
    }

    fun setUniform(name: String, value: Vec3) {
        if (hasUniform(name)) glUniform3f(getUniformLocation(name), value.x, value.y, value.z)
    }

    fun setUniform(name: String, value: Vec4) {
        if (hasUniform(name)) glUniform4f(getUniformLocation(name), value.x, value.y, value.z, value.w)
    }

    fun setUniform(name: String, value: Color) {
        if (hasUniform(name)) glUniform4f(getUniformLocation(name), value.r, value.g, value.b, value.a)
    }

    fun setUniform(name: String, value: Mat3) {
        if (hasUniform(name)) glUniformMatrix3fv(getUniformLocation(name), false, value.toFloatArray())
    }

    fun setUniform(name: String, value: Mat4) {
        if (hasUniform(name)) glUniformMatrix4fv(getUniformLocation(name), false, value.toFloatArray())
    }

    fun setUniform(name: String, texture: Texture, slot: Int = 0) {
        if (hasUniform(name)) {
            texture.bind(slot)
            setUniform(name, slot)
        }
    }

    fun setUniform(name: String, cubeMap: CubeMap, slot: Int = 0) {
        if (hasUniform(name)) {
            cubeMap.bind(slot)
            setUniform(name, slot)
        }
    }

    private fun resolveIncludes(source: String, currentDir: String, visited: MutableSet<String> = mutableSetOf()): String {
        val result = StringBuilder()

        source.lines().forEach { line ->
            val trimmed = line.trim()

            if (trimmed.startsWith("#include")) {
                val includePath = trimmed.substringAfter("\"").substringBefore("\"")
                val fullPath = if (currentDir.isEmpty()) includePath else "$currentDir/$includePath"
                val normalized = AssetUtils.normalize(fullPath)

                if (visited.add(normalized)) {
                    result.appendLine(resolveIncludes(loadSourceFromAnywhere(normalized), normalized.substringBeforeLast("/", ""), visited))
                }
            }
            else {
                result.appendLine(line)
            }
        }
        return result.toString()
    }

    private fun loadSourceFromAnywhere(path: String): String {
        val cleanPath = path.removePrefix("/")
        val userPath = Application.assetsPath.resolve(cleanPath)

        if (Files.exists(userPath)) {
            return readString(userPath)
        }

        val stream = javaClass.getResourceAsStream("/engine_assets/$cleanPath")
            ?: javaClass.getResourceAsStream("/engine_assets/shaders/$cleanPath")
            ?: error("Shader include not found: $path")

        return stream.bufferedReader().use { it.readText() }
    }

    private fun injectDefines(source: String): String {
        val lines = source.lines()

        if (lines.isEmpty()) {
            return source
        }

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

    private fun parseShader(source: String): Map<String, String> {
        val shaders = mutableMapOf<String, String>()
        var currentType: String? = null
        val builder = StringBuilder()

        for (line in source.lines()) {
            if (line.startsWith("#type")) {
                currentType?.let { shaders[it] = builder.toString() }
                builder.clear()
                currentType = line.substringAfter("#type").trim()
            }
            else {
                builder.appendLine(line)
            }
        }

        currentType?.let { shaders[it] = builder.toString() }
        return shaders
    }

    private fun compileShader(type: Int, source: String): Int {
        val id = glCreateShader(type)

        glShaderSource(id, source)
        glCompileShader(id)

        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            val log = glGetShaderInfoLog(id)
            glDeleteShader(id)
            error("Shader compile failed:\n$log")
        }

        return id
    }

    private fun linkShaders(vs: Int, fs: Int): Int {
        val program = glCreateProgram()

        glAttachShader(program, vs)
        glAttachShader(program, fs)
        glLinkProgram(program)

        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            val log = glGetProgramInfoLog(program)
            glDeleteProgram(program)
            error("Shader linking failed:\n$log")
        }

        return program
    }

    override fun bind() = glUseProgram(gpuID)
    override fun unbind() = glUseProgram(0)
    override fun close() = glDeleteProgram(gpuID)
}