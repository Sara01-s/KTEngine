package engine.game

import engine.renderer.Renderer
import engine.renderer.Window
import org.lwjgl.glfw.GLFW.glfwGetTime

class Game(
    val window: Window,
    val renderer: Renderer
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
                fixedUpdate()
                Time.consumeFixedUpdate()
            }

            update()

            renderer.clearScreen()
            draw()

            window.swapBuffers()
            window.pollEvents()
        }
    }
}