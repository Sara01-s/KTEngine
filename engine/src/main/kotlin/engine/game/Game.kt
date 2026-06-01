package engine.game

import engine.assets.Assets
import engine.rendering.Window
import engine.rendering.bindables.RenderTarget
import engine.systems.AudioSystem
import engine.systems.BehaviourSystem
import engine.systems.CameraSystem
import engine.systems.CollisionSystem
import engine.systems.Input
import engine.systems.RenderSystem
import engine.systems.SceneSystem
import engine.systems.ShaderSystem
import engine.utils.PrimitiveMeshes
import engine.utils.log
import org.lwjgl.glfw.GLFW.glfwGetTime

class Game : AutoCloseable {
    companion object {
        val gameRenderTarget by lazy { RenderTarget(Window.width, Window.height, hdr = true) }
        val sceneRenderTarget by lazy { RenderTarget(Window.width, Window.height, hdr = true) }
    }

    init {
        log("Initializing Audio System.")
        AudioSystem.init()
        RenderSystem.init()
        SceneSystem.loadEmptyScene()
    }

    fun loop() {
        while (Window.isOpen()) {
            Window.pollEvents()

            SceneSystem.loadPendingScene()

            Time.update(glfwGetTime())
            Input.update(Time.deltaTime)

            while (Time.shouldRunFixedUpdate()) {
                CollisionSystem.fixedUpdate()
                BehaviourSystem.fixedUpdate()
                Time.consumeFixedUpdate()
            }

            BehaviourSystem.update()
            ShaderSystem.update()

            RenderSystem.render(CameraSystem.main!!, gameRenderTarget)
            RenderSystem.render(CameraSystem.sceneCamera!!, sceneRenderTarget)

            RenderSystem.drawUI()

            Window.swapBuffers()
        }
    }

    override fun close() {
        val systems = listOf(SceneSystem, PrimitiveMeshes, Assets, AudioSystem, BehaviourSystem, CameraSystem)
        systems.reversed().forEach { it.close() }
        Window.close()

        log("Bye Bye.")
    }
}