package engine.game

import engine.assets.Assets
import engine.rendering.Window
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

class Game : AutoCloseable{
    init {
        log("Initializing Audio System.")
        AudioSystem.init()
    }

    fun loop() {
        while (Window.isOpen()) {
            Window.pollEvents()

            SceneSystem.applyPendingScene()

            Time.update(glfwGetTime())
            Input.update(Time.deltaTime)

            while (Time.shouldRunFixedUpdate()) {
                CollisionSystem.update()
                BehaviourSystem.fixedUpdate()
                Time.consumeFixedUpdate()
            }

            BehaviourSystem.update()
            ShaderSystem.update()

            BehaviourSystem.draw()
            RenderSystem.render()

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