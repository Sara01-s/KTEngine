package engine

import engine.game.Game
import engine.rendering.Window
import engine.scenes.MainMenuScene
import engine.scenes.PongScene
import engine.systems.SceneSystem
import engine.utils.log

fun main() {
    log("Creating Window.")
    val window = Window()

    log("Creating Game.")
    val game = Game(window)

    SceneSystem.load(PongScene())

    game.loop(
        fixedUpdate = {
            SceneSystem.currentScene?.fixedUpdate()
        },
        update = {
            SceneSystem.currentScene?.update()
        },
        draw = {
            SceneSystem.currentScene?.draw()
        }
    )

    SceneSystem.close()
    window.close()

    log("Bye Bye.")
}