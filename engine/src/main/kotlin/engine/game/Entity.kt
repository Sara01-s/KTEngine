package engine.game

import engine.components.Component
import engine.components.Transform
import engine.scenes.Scene
import engine.utils.Layers
import engine.utils.Tags
import kotlin.reflect.KClass

class Entity(val id: Int, val name: String = DEFAULT_NAME) : AutoCloseable {
    companion object {
        const val DEFAULT_NAME = "New Entity"
    }

    lateinit var scene: Scene
    var tag = Tags.DEFAULT
    var layerMask = Layers.DEFAULT

    // TODO: Try to make it not accessible.
    val componentsAndItsParents = mutableMapOf<KClass<out Component>, Component>()
    val transform: Transform = addComponent()

    inline fun <reified T : Component> addComponent(): T {
        if (hasComponentOf<T>()) {
            error("Duplicate component ${T::class.simpleName}")
        }

        val component = T::class.java.getDeclaredConstructor().newInstance() as T
        component.entity = this
        component.onAdded()

        val clazz = component::class
        componentsAndItsParents[clazz] = component

        @Suppress("UNCHECKED_CAST")
        var parent = clazz.supertypes.firstOrNull()?.classifier as? KClass<out Component>

        while (parent != null && parent != Component::class) {
            componentsAndItsParents[parent] = component
            @Suppress("UNCHECKED_CAST")
            parent = parent.supertypes.firstOrNull()?.classifier as? KClass<out Component>
        }

        return component
    }

    inline fun <reified T : Component> component(block: T.() -> Unit = {}): T {
        return addComponent<T>().also(block)
    }

    inline fun <reified T : Component> removeComponent() {
        if (!hasComponent<T>()) {
            error("Trying to remove non-existent component ${T::class.simpleName}")
        }

        componentsAndItsParents.remove(T::class)?.close()
    }

    inline fun <reified T : Component> getComponent(): T {
        val component = componentsAndItsParents[T::class] ?: componentsAndItsParents[T::class]

        return component as? T
            ?: error("Component of type ${T::class.simpleName} not found")
    }

    inline fun <reified T : Component> hasComponent(): Boolean {
        return componentsAndItsParents.containsKey(T::class)
    }

    inline fun <reified T : Component> hasComponentOf(): Boolean {
        return componentsAndItsParents.containsKey(T::class)
    }

    fun findEntityByTag(tag: String) : Entity? {
        return scene.entities.find { it.compareTag(tag) }
    }

    fun compareTag(tag: String) : Boolean {
        return this.tag == tag
    }

    fun entity(
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

        transform.addChild(child.transform)
        child.block()

        return child
    }

    override fun close() {
        componentsAndItsParents.values.forEach { it.close() }
        componentsAndItsParents.clear()
    }
}