package engine

import engine.game.Game
import engine.rendering.Window
import engine.scenes.Scene3D
import engine.systems.Assets
import engine.systems.AudioSystem
import engine.systems.SceneSystem
import engine.utils.PrimitiveMeshes
import engine.utils.log

fun main() {
    log("Creating Window.")
    val window = Window()

    log("Creating Game.")
    val game = Game(window)

    log("Initializing Audio System.")
    AudioSystem.init()

    SceneSystem.loadScene(Scene3D())

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
    PrimitiveMeshes.close()
    Assets.close()
    window.close()
    AudioSystem.close()

    log("Bye Bye.")
}