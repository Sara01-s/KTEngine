package engine.components

import engine.rendering.Skybox
import engine.systems.CameraSystem
import engine.systems.RenderSystem
import engine.utils.Color

class Camera : Component() {
    enum class BackgroundMode {
        SolidColor,
        SkyBox,
    }

    var backgroundMode = BackgroundMode.SolidColor
    var skybox: Skybox? = null

    var fov: Float = 45f
    var near: Float = 0.01f
    var far: Float = 1000f

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