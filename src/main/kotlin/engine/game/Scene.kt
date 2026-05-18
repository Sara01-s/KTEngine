package engine.game

import engine.math.AABB
import engine.math.Transform
import engine.renderer.bindables.Shader
import engine.renderer.drawables.Drawable
import engine.renderer.drawables.Square

abstract class Scene : AutoCloseable {

    private val closables = mutableListOf<AutoCloseable>()

    open fun fixedUpdate() {}
    open fun update() {}
    open fun draw() {}

    protected fun createEntity(transform: Transform = Transform(), drawable: Drawable = Square(), collider: AABB = AABB()) : Entity {
        val entity = Entity(transform, drawable, collider)
        closables.add(entity)

        return entity
    }

    protected fun createShader(vertexShaderFilePath: String, fragmentShaderFilePath: String) : Shader {
        val shader = Shader(vertexShaderFilePath, fragmentShaderFilePath)
        closables.add(shader)

        return shader
    }

    final override fun close() {
        closables.reversed().forEach { it.close() }
    }
}