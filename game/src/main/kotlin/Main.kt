package game

import engine.game.Game
import engine.rendering.Window
import engine.systems.RenderSystem
import engine.editor.EditorLayer
import imgui.ImGui

fun main() {
    Game().use { game ->
        val editor = EditorLayer()
        editor.init(Window.handle)

        RenderSystem.addOverlay {
            editor.startFrame()

            ImGui.begin("Status")

            ImGui.text("KTEngine")
            ImGui.separator()
            ImGui.text("FPS: %.1f".format(ImGui.getIO().framerate))
            ImGui.text("Frame Time: %.3f ms".format(1000f / ImGui.getIO().framerate))

            val runtime = Runtime.getRuntime()
            val usedMem = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
            ImGui.text("Memory usage: $usedMem MB")

            ImGui.end()

            editor.endFrame()
        }
        game.loop()
    }
}