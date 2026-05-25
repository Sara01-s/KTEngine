package engine.rendering.text

import engine.components.Renderer
import engine.game.Entity
import engine.rendering.bindables.Material
import engine.rendering.bindables.Mesh
import engine.rendering.bindables.VertexLayout
import engine.systems.Assets
import engine.systems.RenderSystem
import glm_.vec2.Vec2
import glm_.vec3.Vec3

class TextRenderer : Renderer {

    private val layout = VertexLayout()
        .append(VertexLayout.ElementType.Position3D)
        .append(VertexLayout.ElementType.Texture2D)

    override lateinit var entity: Entity
    override var isVisible = true

    var text = "New Text"
        set(value) {
            if (field != value) {
                field = value
                dirty = true
            }
        }

    var font: Font = Assets.loadDefaultFont()
        set(value) {
            if (field != value) {
                field = value
                dirty = true
            }
        }

    var material: Material = Assets.loadDefaultTextMaterial()

    private var mesh = Mesh(layout = layout, vertices = emptyList(), indices = intArrayOf())
    private var dirty = true

    override fun draw() {
        if (!isVisible) {
            return
        }

        if (dirty) {
            rebuildMesh()
        }

        material.bind()
        material.setTexture("_MainTex", font.texture)
        material.setMat4("_MVP", RenderSystem.calculateMvpMatrix(entity.transform))

        mesh.draw()
    }

    private fun rebuildMesh() {
        val printableChars = text.count { it != '\n' && it != ' ' && font.glyphs.containsKey(it) }

        if (text.isEmpty() || printableChars == 0) {
            mesh.setData(emptyList(), intArrayOf())
            dirty = false
            return
        }

        val vertices = mutableListOf<Any>()
        val indices = mutableListOf<Int>()

        var cursorX = 0f
        var cursorY = 0f
        var vertexOffset = 0

        for (char in text) {
            if (char == '\n') {
                cursorX = 0f
                cursorY -= font.lineHeight
                continue
            }

            if (char == ' ') {
                cursorX += font.glyphs[' ']?.advance ?: (font.lineHeight * 0.5f)
                continue
            }

            val glyph = font.glyphs[char] ?: continue

            val x = cursorX + glyph.offset.x
            val y = cursorY - glyph.offset.y
            val w = glyph.size.x
            val h = glyph.size.y
            val uvMin = glyph.uvMin
            val uvMax = glyph.uvMax

            vertices.addAll(listOf(
                Vec3(x,     y,     0f), Vec2(uvMin.x, uvMax.y),
                Vec3(x + w, y,     0f), Vec2(uvMax.x, uvMax.y),
                Vec3(x + w, y - h, 0f), Vec2(uvMax.x, uvMin.y),
                Vec3(x,     y - h, 0f), Vec2(uvMin.x, uvMin.y),
            ))

            indices.addAll(listOf(
                vertexOffset + 0, vertexOffset + 1, vertexOffset + 2,
                vertexOffset + 2, vertexOffset + 3, vertexOffset + 0,
            ))

            vertexOffset += 4
            cursorX += glyph.advance
        }

        mesh.setData(vertices, indices.toIntArray())
        dirty = false
    }

    override fun onAdded() = RenderSystem.register(this)
    override fun onRemoved() = RenderSystem.unregister(this)

    override fun close() {
        mesh.close()
        onRemoved()
    }
}