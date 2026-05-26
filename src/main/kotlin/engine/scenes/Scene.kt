package engine.scenes

import engine.game.Entity

abstract class Scene : AutoCloseable {

    private val entities = mutableListOf<Entity>()

    open fun fixedUpdate() {}
    open fun update() {}
    open fun draw() {}

    protected fun createEntity() : Entity {
        val entity = Entity()
        entities.add(entity)

        return entity
    }

    final override fun close() {
        entities.reversed().forEach { it.close() }
    }
}