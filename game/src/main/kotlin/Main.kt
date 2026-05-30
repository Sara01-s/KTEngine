package engine

import engine.game.Game
import engine.rendering.Window
import engine.scenes.Scene1
import engine.assets.Assets
import engine.systems.AudioSystem
import engine.systems.SceneSystem
import engine.utils.PrimitiveMeshes
import engine.utils.log

fun main() {
    log("Creating Game.")
    val game = Game()

    log("Initializing Audio System.")
    AudioSystem.init()

    SceneSystem.loadScene(Scene1())

    game.loop()

    val systems = listOf(SceneSystem, PrimitiveMeshes, Assets, AudioSystem)
    systems.reversed().forEach { it.close() }
    Window.close()

    log("Bye Bye.")
}