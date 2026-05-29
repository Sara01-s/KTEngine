package engine.game

import engine.rendering.Window
import engine.systems.CollisionSystem
import engine.systems.Input
import engine.systems.RenderSystem
import engine.systems.SceneSystem
import org.lwjgl.glfw.GLFW.glfwGetTime

class Game {
    inline fun loop(
        crossinline fixedUpdate: () -> Unit = {},
        crossinline update: () -> Unit = {},
        crossinline draw: () -> Unit = {}
    ) {
        while (Window.isOpen()) {
            Window.pollEvents()

            SceneSystem.applyPendingScene()

            Time.update(glfwGetTime())
            Input.update(Time.deltaTime)

            while (Time.shouldRunFixedUpdate()) {
                CollisionSystem.update()
                fixedUpdate()
                Time.consumeFixedUpdate()
            }

            update()

            RenderSystem.render()
            draw()

            Window.swapBuffers()
        }
    }
}