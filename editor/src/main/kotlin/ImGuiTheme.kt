package engine.editor

import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiDir

fun applyTheme() {
    val style = ImGui.getStyle()

    style.alpha = 1.0f
    style.disabledAlpha = 0.3f
    style.setWindowPadding(4f, 8f)
    style.setFramePadding(4f, 4f)
    style.setItemSpacing(8f, 2f)
    style.setItemInnerSpacing(4f, 4f)
    style.indentSpacing = 16f
    style.scrollbarSize = 18f
    style.grabMinSize = 20f

    style.windowBorderSize = 1f
    style.childBorderSize = 1f
    style.popupBorderSize = 1f
    style.frameBorderSize = 0f

    style.windowRounding = 4f
    style.childRounding = 6f
    style.frameRounding = 4f
    style.popupRounding = 4f
    style.scrollbarRounding = 12f
    style.grabRounding = 4f

    style.tabBorderSize = 0f
    style.tabRounding = 1f

    style.setCellPadding(8.0f, 4.0f)
    style.setWindowTitleAlign(0.5f, 0.5f)
    style.windowMenuButtonPosition = ImGuiDir.Right
    style.colorButtonPosition = ImGuiDir.Right
    style.setButtonTextAlign(0.5f, 0.5f)
    style.setSelectableTextAlign(0.5f, 0.5f)

    // Colors
    style.setColor(ImGuiCol.Text, 1f, 1f, 1f, 1f)
    style.setColor(ImGuiCol.TextDisabled, 0.39f, 0.39f, 0.39f, 1f)
    style.setColor(ImGuiCol.WindowBg, 0.08f, 0.09f, 0.11f, 1.00f)
    style.setColor(ImGuiCol.ChildBg, 0.09f, 0.10f, 0.12f, 1.00f)
    style.setColor(ImGuiCol.PopupBg, 0.08f, 0.09f, 0.11f, 1.00f)
    style.setColor(ImGuiCol.Border, 0.16f, 0.17f, 0.19f, 1.00f)
    style.setColor(ImGuiCol.BorderShadow, 0.08f, 0.09f, 0.11f, 1.00f)
    style.setColor(ImGuiCol.FrameBg, 0.11f, 0.13f, 0.15f, 1.00f)
    style.setColor(ImGuiCol.FrameBgHovered, 0.16f, 0.17f, 0.19f, 1.00f)
    style.setColor(ImGuiCol.FrameBgActive, 0.16f, 0.17f, 0.19f, 1.00f)
    style.setColor(ImGuiCol.TitleBg, 0.04f, 0.05f, 0.07f, 1.00f)
    style.setColor(ImGuiCol.TitleBgActive, 0.04f, 0.05f, 0.07f, 1.00f)
    style.setColor(ImGuiCol.TitleBgCollapsed, 0.08f, 0.09f, 0.11f, 1.00f)
    style.setColor(ImGuiCol.MenuBarBg, 0.10f, 0.11f, 0.13f, 1.00f)
    style.setColor(ImGuiCol.ScrollbarBg, 0.04f, 0.05f, 0.07f, 1.00f)
    style.setColor(ImGuiCol.ScrollbarGrab, 0.12f, 0.13f, 0.15f, 1.00f)
    style.setColor(ImGuiCol.ScrollbarGrabHovered, 0f, 0.47f, 0.63f, 1f)
    style.setColor(ImGuiCol.ScrollbarGrabActive, 0f, 0.75f, 1f, 1f)
    style.setColor(ImGuiCol.CheckMark, 0f, 0.75f, 1f, 1f)
    style.setColor(ImGuiCol.SliderGrab, 0f, 0.47f, 0.63f, 1f)
    style.setColor(ImGuiCol.SliderGrabActive, 0f, 0.75f, 1f, 1f)
    style.setColor(ImGuiCol.Button, 0.10f, 0.11f, 0.14f, 1.00f)
    style.setColor(ImGuiCol.ButtonHovered, 0f, 0.47f, 0.63f, 1f)
    style.setColor(ImGuiCol.ButtonActive, 0.18f, 0.19f, 0.21f, 1.00f)
    style.setColor(ImGuiCol.Header, 0.12f, 0.13f, 0.15f, 1.00f)
    style.setColor(ImGuiCol.HeaderHovered, 0f, 0.47f, 0.63f, 1f)
    style.setColor(ImGuiCol.HeaderActive, 0f, 0.75f, 1f, 1f)
    style.setColor(ImGuiCol.Separator, 0.13f, 0.15f, 0.19f, 1f)
    style.setColor(ImGuiCol.SeparatorHovered, 0f, 0.47f, 0.63f, 1f)
    style.setColor(ImGuiCol.SeparatorActive, 0f, 0.75f, 1f, 1f)
    style.setColor(ImGuiCol.ResizeGrip, 0.15f, 0.15f, 0.15f, 1f)
    style.setColor(ImGuiCol.ResizeGripHovered, 0f, 0.47f, 0.63f, 1f)
    style.setColor(ImGuiCol.ResizeGripActive, 0f, 0.75f, 1f, 1f)
    style.setColor(ImGuiCol.Tab, 0.08f, 0.09f, 0.11f, 1.00f)
    style.setColor(ImGuiCol.TabHovered, 0.12f, 0.13f, 0.15f, 1.00f)
    style.setColor(ImGuiCol.TabActive, 0.12f, 0.13f, 0.15f, 1.00f)
    style.setColor(ImGuiCol.DockingPreview, 0.08f, 0.09f, 0.11f, 1.00f)
    style.setColor(ImGuiCol.PlotLines, 0.18f, 0.19f, 0.21f, 1.00f)
    style.setColor(ImGuiCol.PlotLinesHovered, 0f, 0.47f, 0.63f, 1f)
    style.setColor(ImGuiCol.PlotHistogram, 0.18f, 0.19f, 0.21f, 1.00f)
    style.setColor(ImGuiCol.PlotHistogramHovered, 0f, 0.47f, 0.63f, 1f)
}