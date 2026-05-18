package engine

import engine.game.Game
import engine.renderer.Color
import engine.renderer.Renderer
import engine.renderer.Window
import engine.game.SceneManager
import engine.scenes.MainMenuScene
import engine.scenes.PongScene
import engine.utils.log

fun main() {
    log("Creating Window.")
    val window = Window()

    log("Creating Game.")
    val game = Game(window)

    SceneManager.load(PongScene())

    game.loop(
        fixedUpdate = {
            SceneManager.currentScene?.fixedUpdate()
        },
        update = {
            SceneManager.currentScene?.update()
        },
        draw = {
            SceneManager.currentScene?.draw()
        }
    )

    SceneManager.close()
    window.close()

    log("Bye Bye.")
}