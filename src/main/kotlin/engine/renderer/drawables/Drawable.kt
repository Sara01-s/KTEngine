package engine.renderer.drawables

import engine.components.Transform
import engine.renderer.Renderer
import engine.renderer.bindables.Bindable
import engine.renderer.bindables.Material
import engine.utils.GLDebug.glCall
import org.lwjgl.opengl.GL11.glDrawElements

data class DrawCommand(
    val mode: Int,
    val count: Int,
    val type: Int,
    val offset: Long
)

open class Drawable(val material: Material, val transform: Transform) : AutoCloseable {
    var bindables = mutableListOf<Bindable>()
    var drawCommand: DrawCommand? = null

    fun setBindables(vararg bindables: Bindable) {
        this.bindables.addAll(bindables)
    }

    fun draw() {
        for (bindable in bindables) {
            bindable.bind()
        }

        material.bind()
        material.setMat4("_MVP", Renderer.calculateMvpMatrix(transform))

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