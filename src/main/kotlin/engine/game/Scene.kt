package engine.game

abstract class Scene : AutoCloseable {

    private val closables = mutableListOf<AutoCloseable>()

    open fun fixedUpdate() {}
    open fun update() {}
    open fun draw() {}

    protected fun createEntity() : Entity {
        val entity = Entity()
        closables.add(entity)

        return entity
    }

    final override fun close() {
        closables.reversed().forEach { it.close() }
    }
}