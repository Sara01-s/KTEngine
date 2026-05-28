package engine.game

import engine.rendering.Window
import engine.systems.CollisionSystem
import engine.systems.Input
import engine.systems.RenderSystem
import engine.systems.SceneSystem
import org.lwjgl.glfw.GLFW.glfwGetTime

class Game(
    val window: Window,
) {
    init {
        Input.init(window)
    }

    inline fun loop(
        crossinline fixedUpdate: () -> Unit = {},
        crossinline update: () -> Unit = {},
        crossinline draw: () -> Unit = {}
    ) {
        while (window.isOpen()) {
            window.pollEvents()

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

            SceneSystem.applyPendingScene()

            window.swapBuffers()
        }
    }
}