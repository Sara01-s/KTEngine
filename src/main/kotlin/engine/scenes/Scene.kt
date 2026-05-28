package engine.scenes

import engine.game.Entity
import engine.components.Transform
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KProperty

abstract class Scene : AutoCloseable {

    companion object {
        @PublishedApi
        internal val idSequence = AtomicInteger(0)

        @PublishedApi
        internal fun generateNextId(): Int = idSequence.getAndIncrement()

        private fun resetSequence() { idSequence.set(0) }
    }

    val rootEntity: Entity = Entity(generateNextId(), "Root")

    @PublishedApi
    internal val entityMap = HashMap<Int, Entity>()

    @PublishedApi
    internal val namedRefsMap = HashMap<String, Entity>()

    init {
        entityMap[rootEntity.id] = rootEntity
    }

    open fun start() {}
    open fun fixedUpdate() {}
    open fun update() {}
    open fun draw() {}

    protected fun createEntity(name: String = Entity.DEFAULT_NAME): Entity {
        val nextId = generateNextId()
        val entity = Entity(nextId, name).apply {
            scene = this@Scene
        }

        rootEntity.transform.addChild(entity.transform)
        entityMap[nextId] = entity

        if (name != Entity.DEFAULT_NAME) {
            namedRefsMap[name] = entity
        }

        return entity
    }

    protected inline fun entity(name: String = Entity.DEFAULT_NAME, block: Entity.() -> Unit): Entity {
        val entity = createEntity(name)
        entity.block()
        return entity
    }

    fun getEntity(id: Int): Entity {
        return entityMap[id] ?: error("Entity with id $id not found")
    }

    protected fun entityRef(name: String): EntityNameDelegate {
        return EntityNameDelegate(this, name)
    }

    protected class EntityNameDelegate(private val scene: Scene, private val name: String) {
        private var cachedEntity: Entity? = null

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Entity {
            if (cachedEntity != null) {
                return cachedEntity!!
            }

            val entity = scene.namedRefsMap[name]
                ?: error("Entity with name '$name' was not registered or uses DEFAULT_NAME.")

            cachedEntity = entity
            return entity
        }
    }

    final override fun close() {
        entityMap.clear()
        namedRefsMap.clear()
        rootEntity.close()
        resetSequence()
    }
}