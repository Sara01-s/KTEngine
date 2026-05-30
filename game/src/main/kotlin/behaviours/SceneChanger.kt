package engine.behaviours

import engine.components.Behaviour
import engine.scenes.Scene
import engine.scenes.Scene1
import engine.scenes.Scene2
import engine.systems.Input
import engine.systems.Key
import engine.systems.SceneSystem
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

class SceneChanger : Behaviour() {

    override fun update() {
        handleSceneHotkeys()
    }

     private fun handleSceneHotkeys(vararg scenes: Pair<Key, () -> Scene>) {
        val sceneMap = if (scenes.isEmpty()) defaultSceneHotkeys() else mapOf(*scenes)
        sceneMap.forEach { (key, factory) ->
            if (Input.Keyboard.isJustPressed(key)) {
                SceneSystem.loadScene(factory())
            }
        }
    }

    private fun defaultSceneHotkeys(): Map<Key, () -> Scene> = mapOf(
        Key.Alpha1 to { Scene1() },
        Key.Alpha2 to { Scene2() },
    )
}