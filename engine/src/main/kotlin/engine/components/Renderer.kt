package engine.components

abstract class Renderer : Component() {
    open var isVisible: Boolean = true
    abstract fun draw(camera: Camera, aspect: Float)
}