package engine.renderer.drawables

import engine.math.Transform
import engine.renderer.Renderer
import engine.renderer.bindables.Bindable
import engine.renderer.bindables.Shader
import engine.utils.GLDebug.glCall
import org.lwjgl.opengl.GL11.glDrawElements
import java.lang.AutoCloseable

data class DrawCommand(
    val mode: Int,
    val count: Int,
    val type: Int,
    val offset: Long
)

open class Drawable(val shader: Shader) : AutoCloseable {
    var transform: Transform = Transform()
    var bindables = mutableListOf<Bindable>()
    var drawCommand: DrawCommand? = null

    fun setBindables(vararg bindables: Bindable) {
        this.bindables.addAll(bindables)
    }

    fun draw() {
        for (bindable in bindables) {
            bindable.bind()
        }

        shader.bind()
        shader.setUniform("u_MVP", Renderer.getMvpMatrix(transform))

        val cmd = drawCommand ?: error("drawCommand is null")

        glCall { glDrawElements(cmd.mode, cmd.count, cmd.type, cmd.offset) }
    }

    override fun close() {
        if (bindables.isEmpty()) {
            return
        }

        for (bindable in bindables) {
            bindable.close()
        }
    }
}