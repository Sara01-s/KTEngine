package engine.game

import engine.components.Component
import engine.components.Transform
import kotlin.reflect.KClass

class Entity : AutoCloseable {
    val transform = Transform()

    private val components = mutableMapOf<KClass<out Component>, Component>().apply {
        this[Transform::class] = Transform()
    }

    internal inline fun <reified T : Component> addComponent() : T {
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

    internal inline fun <reified T : Component> getComponent() : T {
        val component = components[T::class]

        if (component != null) {
            return component as T
        }
        else {
            error("Component not found")
        }
    }

    internal inline fun <reified T : Component> hasComponents(): Boolean {
        return components.isNotEmpty() && components.containsKey(T::class)
    }

    override fun close() {
        components.values.forEach { it.close() }
        components.clear()
    }
}