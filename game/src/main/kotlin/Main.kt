package game

import engine.game.Game
import engine.rendering.Window
import engine.scenes.Scene1
import engine.systems.RenderSystem
import engine.systems.SceneSystem
import engine.editor.EditorLayer
import imgui.ImGui

fun main() {
    Game().use { game ->
        SceneSystem.loadScene(Scene1())
        val editor = EditorLayer()
        editor.init(Window.handle)

        RenderSystem.addOverlay {
            editor.startFrame()

            ImGui.begin("Inspector")

            ImGui.text("KTEngine")
            ImGui.separator()
            ImGui.text("FPS: %.1f".format(ImGui.getIO().framerate))
            ImGui.text("Frame Time: %.3f ms".format(1000f / ImGui.getIO().framerate))

            val runtime = Runtime.getRuntime()
            val usedMem = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
            ImGui.text("Memoria usage: $usedMem MB")

            ImGui.showDemoWindow()

            ImGui.end()

            editor.endFrame()
        }
        game.loop()
    }
}