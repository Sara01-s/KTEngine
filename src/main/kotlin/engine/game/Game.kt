package engine.game

import engine.rendering.Window
import engine.systems.CollisionSystem
import engine.systems.RenderSystem
import org.lwjgl.glfw.GLFW.glfwGetTime

class Game(
    val window: Window,
) {
    inline fun loop(
        crossinline fixedUpdate: () -> Unit = {},
        crossinline update: () -> Unit = {},
        crossinline draw: () -> Unit = {}
    ) {
        while (window.isOpen()) {
            Time.update(glfwGetTime())
            Input.update(window, Time.deltaTime)

            while (Time.shouldRunFixedUpdate()) {
                CollisionSystem.update()
                fixedUpdate()
                Time.consumeFixedUpdate()
            }

            update()

            RenderSystem.clearScreen()
            RenderSystem.render()
            draw()

            window.swapBuffers()
            window.pollEvents()
        }
    }
}