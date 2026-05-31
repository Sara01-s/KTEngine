package engine.game

import engine.rendering.Window
import engine.systems.BehaviourSystem
import engine.systems.CollisionSystem
import engine.systems.Input
import engine.systems.RenderSystem
import engine.systems.SceneSystem
import engine.systems.ShaderSystem
import org.lwjgl.glfw.GLFW.glfwGetTime

class Game {
    fun loop() {
        while (Window.isOpen()) {
            Window.pollEvents()

            SceneSystem.applyPendingScene()

            Time.update(glfwGetTime())
            Input.update(Time.deltaTime)

            while (Time.shouldRunFixedUpdate()) {
                CollisionSystem.update()
                BehaviourSystem.fixedUpdate()
                SceneSystem.currentScene?.fixedUpdate()
                Time.consumeFixedUpdate()
            }

            BehaviourSystem.update()
            ShaderSystem.update()
            SceneSystem.currentScene?.update()

            RenderSystem.render()
            BehaviourSystem.draw()
            SceneSystem.currentScene?.draw()

            Window.swapBuffers()
        }
    }
}