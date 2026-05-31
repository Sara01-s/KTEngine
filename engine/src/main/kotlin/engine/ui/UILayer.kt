package engine.ui

interface UILayer : AutoCloseable{
    fun init(windowHandle: Long)
    fun startFrame()
    fun endFrame()
}