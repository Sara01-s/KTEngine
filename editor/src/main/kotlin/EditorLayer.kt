package engine.editor

import engine.Application
import imgui.ImGui
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import engine.ui.UILayer
import imgui.flag.ImGuiConfigFlags

class EditorLayer : UILayer {
    private val imGuiGlfw = ImGuiImplGlfw()
    private val imGuiGl3 = ImGuiImplGl3()

    override fun init(windowHandle: Long) {
        ImGui.createContext()
        val io = ImGui.getIO()

        val fontPath = Application.assetsPath.resolve("fonts/Inter-VariableFont_opsz,wght.ttf").toString()
        io.fonts.addFontFromFileTTF(fontPath, 32f)

        applyTheme()

        imGuiGlfw.init(windowHandle, true)
        imGuiGl3.init("#version 430")
    }

    override fun startFrame() {
        imGuiGlfw.newFrame()
        ImGui.newFrame()
    }

    override fun endFrame() {
        ImGui.render()
        imGuiGl3.renderDrawData(ImGui.getDrawData())
    }

    override fun close() {
        imGuiGl3.dispose()
        imGuiGlfw.dispose()
        ImGui.destroyContext()
    }
}