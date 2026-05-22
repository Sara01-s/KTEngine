package engine.components

import engine.game.Entity
import glm_.vec3.Vec3

class Transform(
    var position: Vec3 = Vec3(0f),
    var scale: Vec3 = Vec3(1f),
) : Component {

    override lateinit var entity: Entity
}