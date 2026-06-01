package engine.scenes

import engine.game.Entity
import java.util.concurrent.atomic.AtomicInteger

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

    abstract fun create()

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

    final override fun close() {
        entityMap.clear()
        namedRefsMap.clear()
        rootEntity.close()
        resetSequence()
    }
}