package engine.game

import engine.components.Component
import engine.components.Transform
import engine.scenes.Scene
import kotlin.reflect.KClass

class Entity(val id: Int, val name: String = DEFAULT_NAME) : AutoCloseable {
    companion object {
        const val DEFAULT_NAME = "New Entity"
    }

    lateinit var scene: Scene

    val components = mutableMapOf<KClass<out Component>, Component>()
    val transform: Transform = addComponent()

    internal inline fun <reified T : Component> addComponent(): T {
        val component = T::class.java.getDeclaredConstructor().newInstance()
        component.entity = this
        component.onAdded()

        components[T::class] = component
        return component
    }

    internal inline fun <reified T : Component> removeComponent() {
        getComponent<T>().close()
        components.remove(T::class)
    }

    internal inline fun <reified T : Component> getComponent(): T {
        return components[T::class] as? T
            ?: error("Component not found: ${T::class}")
    }

    internal inline fun <reified T : Component> hasComponent(): Boolean {
        return components.containsKey(T::class)
    }

    fun childEntity(
        name: String = DEFAULT_NAME,
        block: Entity.() -> Unit = {}
    ): Entity {
        val scene = this.scene
        val nextId = Scene.idSequence.getAndIncrement()

        val child = Entity(nextId, name).apply {
            this.scene = scene
        }

        scene.entityMap[nextId] = child

        if (name != DEFAULT_NAME) {
            scene.namedRefsMap[name] = child
        }

        this.transform.addChild(child.transform)
        child.block()

        return child
    }

    override fun close() {
        components.values.forEach { it.close() }
        components.clear()
    }
}