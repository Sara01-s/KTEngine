package game

import engine.game.Game
import engine.rendering.Window
import engine.systems.RenderSystem
import engine.editor.EditorLayer
import engine.game.Game.Companion.gameRenderTarget
import engine.game.Game.Companion.sceneRenderTarget
import imgui.ImGui

fun showGame() {
    ImGui.begin("Game")
    val viewportSize = ImGui.getContentRegionAvail()
    val newWidth = viewportSize.x.toInt()
    val newHeight = viewportSize.y.toInt()

    if (newWidth > 0 && newHeight > 0 &&
        (newWidth != gameRenderTarget.width || newHeight != gameRenderTarget.height)) {
        gameRenderTarget.resize(newWidth, newHeight)
    }

    ImGui.image(
        gameRenderTarget.textureGpuID,
        newWidth.toFloat(),
        newHeight.toFloat(),
        0f, 1f, 1f, 0f
    )
    ImGui.end()
}

fun showScene() {
    ImGui.begin("Scene")

    val viewportSize = ImGui.getContentRegionAvail()
    val newWidth = viewportSize.x.toInt()
    val newHeight = viewportSize.y.toInt()

    if (newWidth > 0 && newHeight > 0 &&
        (newWidth != sceneRenderTarget.width || newHeight != sceneRenderTarget.height)) {
        sceneRenderTarget.resize(newWidth, newHeight)
    }

    ImGui.image(
        sceneRenderTarget.textureGpuID,
        newWidth.toFloat(),
        newHeight.toFloat(),
        0f, 1f, 1f, 0f
    )

    ImGui.end()
}

fun showStatus() {
    ImGui.begin("Status")

    ImGui.text("KTEngine")
    ImGui.separator()
    ImGui.text("FPS: %.1f".format(ImGui.getIO().framerate))
    ImGui.text("Frame Time: %.3f ms".format(1000f / ImGui.getIO().framerate))

    val runtime = Runtime.getRuntime()
    val usedMem = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
    ImGui.text("Memory usage: $usedMem MB")

    ImGui.end()
}

fun main() {
    Game().use { game ->
        val editor = EditorLayer()
        editor.init(Window.handle)

        RenderSystem.addOverlay {
            editor.startFrame()

            editor.setupDockSpace()

            showScene()
            showGame()
            showStatus()

            editor.endFrame()
        }

        game.loop()
    }
}