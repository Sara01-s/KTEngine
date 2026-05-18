package engine.game

import engine.renderer.Renderer
import engine.renderer.Window
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

            Renderer.clearScreen()
            Renderer.drawAllDrawables()
            draw()

            window.swapBuffers()
            window.pollEvents()
        }
    }
}