package engine.editor

import engine.Application
import imgui.ImGui
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import engine.ui.UILayer
import imgui.flag.ImGuiConfigFlags
import imgui.flag.ImGuiDir
import imgui.flag.ImGuiWindowFlags
import imgui.internal.ImGui.dockBuilderAddNode
import imgui.internal.ImGui.dockBuilderDockWindow
import imgui.internal.ImGui.dockBuilderFinish
import imgui.internal.ImGui.dockBuilderRemoveNode
import imgui.internal.ImGui.dockBuilderSetNodeSize
import imgui.internal.ImGui.dockBuilderSplitNode
import imgui.internal.flag.ImGuiDockNodeFlags
import org.lwjgl.glfw.GLFW

class EditorLayer : UILayer {
    private val imGuiGlfw = ImGuiImplGlfw()
    private val imGuiGl3 = ImGuiImplGl3()

    override fun init(windowHandle: Long) {
        ImGui.createContext()
        val io = ImGui.getIO()

        io.addConfigFlags(ImGuiConfigFlags.DockingEnable)
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable)

        val fontPath = Application.assetsPath.resolve("fonts/Inter-VariableFont_opsz,wght.ttf").toString()
        io.fonts.addFontFromFileTTF(fontPath, 24f)

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

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val backupCurrentContext = GLFW.glfwGetCurrentContext()
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            GLFW.glfwMakeContextCurrent(backupCurrentContext)
        }
    }

    fun setupDockSpace() {
        val windowFlags =
            ImGuiWindowFlags.MenuBar or
            ImGuiWindowFlags.NoDocking or
            ImGuiWindowFlags.NoTitleBar or
            ImGuiWindowFlags.NoCollapse or
            ImGuiWindowFlags.NoResize or
            ImGuiWindowFlags.NoBringToFrontOnFocus or
            ImGuiWindowFlags.NoNavFocus

        val viewport = ImGui.getMainViewport()
        ImGui.setNextWindowPos(viewport.workPos.x, viewport.workPos.y)
        ImGui.setNextWindowSize(viewport.workSize.x, viewport.workSize.y)
        ImGui.setNextWindowViewport(viewport.id)

        ImGui.begin("DockSpace", windowFlags)

        val dockSpaceId = ImGui.getID("MyDockSpace")

        dockBuilderRemoveNode(dockSpaceId)
        dockBuilderAddNode(dockSpaceId, ImGuiDockNodeFlags.DockSpace)
        dockBuilderSetNodeSize(dockSpaceId, viewport.workSize.x, viewport.workSize.y)

        val left = dockBuilderSplitNode(dockSpaceId, ImGuiDir.Left, 0.2f, null, null)

        dockBuilderDockWindow("Scene", dockSpaceId)
        dockBuilderDockWindow("Game", dockSpaceId)
        dockBuilderDockWindow("Status", left)

        dockBuilderFinish(dockSpaceId)

        ImGui.dockSpace(dockSpaceId, 0f, 0f, ImGuiDockNodeFlags.None)
        ImGui.end()
    }

    override fun close() {
        imGuiGl3.dispose()
        imGuiGlfw.dispose()
        ImGui.destroyContext()
    }
}