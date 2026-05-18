package engine.components

import engine.game.Entity

interface Component : AutoCloseable {
    var entity : Entity

    fun onAdded() {}
    fun onRemoved() {}

    override fun close() {}
}