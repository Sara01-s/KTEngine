package engine.components

import engine.game.Entity
import engine.rendering.Skybox
import engine.utils.Color
import engine.utils.Layers

class Camera : Component() {
    enum class BackgroundMode { SolidColor, SkyBox }

    var cullingMask = Layers.EVERYTHING
    var backgroundMode = BackgroundMode.SolidColor

    var backgroundColor: Color = Color.gray20
    var skybox: Skybox? = null

    var fov: Float = 45f
    var near: Float = 0.01f
    var far: Float = 1000f

    fun shouldRender(entity: Entity): Boolean {
        return (entity.layerMask and cullingMask) != 0
    }
}