package engine.components

import engine.game.Entity
import engine.rendering.Skybox
import engine.systems.CameraSystem
import engine.systems.RenderSystem
import engine.utils.Color

class Camera : Component {
    enum class BackgroundMode {
        SolidColor,
        SkyBox,
    }

    override lateinit var entity: Entity

    var backgroundMode: BackgroundMode = BackgroundMode.SolidColor
    var pitch = 0f
    var yaw = 0f
    var roll = 0f

    fun setBackgroundColor(color: Color) {
        if (backgroundMode == BackgroundMode.SolidColor) {
            RenderSystem.setClearColor(color)
        }
        else error("Cannot set background color, background mode is set to skybox.")
    }

    fun setSkyBox(skybox: Skybox) {
        if (backgroundMode == BackgroundMode.SkyBox) {
            RenderSystem.skybox = skybox
        }
        else error("Cannot set skybox, background mode is set to solid color.")
    }

    override fun onAdded() {
        CameraSystem.register(this)
    }

    override fun onRemoved() {
        CameraSystem.unregister(this)
    }
}