package engine.components

import engine.game.Entity
import glm_.vec2.Vec2

class Transform(
    var position: Vec2 = Vec2(0f, 0f),
    var scale: Vec2 = Vec2(1f, 1f),
) : Component {

    override lateinit var entity: Entity
}