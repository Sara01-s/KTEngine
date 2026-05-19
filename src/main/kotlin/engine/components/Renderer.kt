package engine.components

interface Renderer : Component {
    var isVisible: Boolean
    fun draw()
}