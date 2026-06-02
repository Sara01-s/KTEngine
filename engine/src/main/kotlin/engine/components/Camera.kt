package engine.components

import engine.game.Entity
import engine.rendering.Skybox
import engine.utils.Color
import engine.utils.Layers

class Camera : Component() {
    sealed interface Background {
        data class SolidColor(val color: Color) : Background
        data class SkyBox(val skybox: Skybox) : Background
    }

    var cullingMask = Layers.EVERYTHING
    var background: Background = Background.SolidColor(Color.gray20)

    var fov: Float = 45f
    var near: Float = 0.01f
    var far: Float = 1000f

    fun shouldRender(entity: Entity): Boolean {
        return (entity.layerMask and cullingMask) != 0
    }
}
