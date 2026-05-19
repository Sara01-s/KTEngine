package engine.rendering.text

import engine.components.Renderer
import engine.game.Entity
import engine.rendering.bindables.Material
import engine.rendering.bindables.Mesh
import engine.systems.Assets
import engine.systems.RenderSystem
import org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW

class TextRenderer : Renderer {

    override lateinit var entity: Entity
    override var isVisible = true

    var text = "New Text"
        set(value) {
            field = value
            dirty = true
        }

    var font: Font = Assets.loadDefaultFont()
        set(value) {
            field = value
            dirty = true
        }

    var material: Material = Assets.loadDefaultTextMaterial()

    private var mesh = Mesh(
        floatArrayOf(),
        intArrayOf(),
        usage = GL_DYNAMIC_DRAW
    )

    private var dirty = true

    override fun draw() {
        if (dirty) {
            rebuildMesh()
        }

        material.bind()
        material.setTexture("_MainTex", font.texture)
        material.setMat4("_MVP", RenderSystem.calculateMvpMatrix(entity.transform))

        mesh.draw()
    }

    private fun rebuildMesh() {
        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Int>()

        var cursorX = 0f
        var cursorY = 0f

        var vertexOffset = 0

        for (char in text) {
            if (char == '\n') {
                cursorX = 0f
                cursorY -= 1f
                continue
            }

            if (char == ' ') {
                cursorX += font.glyphs[' ']?.advance ?: (font.lineHeight * 0.5f)
                continue
            }

            val glyph = font.glyphs[char]
                ?: continue

            val x = cursorX + glyph.offset.x
            val y = cursorY - glyph.offset.y

            val w = glyph.size.x
            val h = glyph.size.y

            val uvMin = glyph.uvMin
            val uvMax = glyph.uvMax

            vertices.addAll(listOf(
                // pos        // uv
                x,     y,     uvMin.x, uvMax.y,
                x + w, y,     uvMax.x, uvMax.y,
                x + w, y - h, uvMax.x, uvMin.y,
                x,     y - h, uvMin.x, uvMin.y
            ))

            indices.addAll(
                listOf(
                    vertexOffset + 0,
                    vertexOffset + 1,
                    vertexOffset + 2,

                    vertexOffset + 2,
                    vertexOffset + 3,
                    vertexOffset + 0
                )
            )

            vertexOffset += 4
            cursorX += glyph.advance
        }

        mesh.setData(vertices.toFloatArray(), indices.toIntArray())
        dirty = false
    }

    override fun onAdded() {
        RenderSystem.register(this)
    }

    override fun onRemoved() {
        RenderSystem.unregister(this)
    }

    override fun close() {
        mesh.close()
        onRemoved()
    }
}