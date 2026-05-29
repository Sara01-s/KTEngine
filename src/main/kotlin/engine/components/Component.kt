package engine.components

import engine.game.Entity

abstract class Component : AutoCloseable {
    lateinit var entity : Entity

    open fun onAdded() {}
    open fun onRemoved() {}

    override fun close() {}
}