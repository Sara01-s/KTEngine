package engine.rendering.text

import engine.components.Renderer
import engine.game.Entity
import engine.rendering.bindables.Material
import engine.rendering.bindables.Mesh
import engine.rendering.bindables.Vertex
import engine.rendering.bindables.VertexLayout
import engine.systems.Assets
import engine.systems.RenderSystem
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TextRenderer : Renderer {

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

    private val layout = VertexLayout()
        .append(VertexLayout.ElementType.Position3D)
        .append(VertexLayout.ElementType.Texture2D)

    private var mesh = Mesh(
        vertexBuffer = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder()),
        indices = intArrayOf(),
        layout = layout
    )

    private var dirty = true

    override fun draw() {
        if (!isVisible) return

        if (dirty) {
            rebuildMesh()
        }

        material.bind()
        material.setTexture("_MainTex", font.texture)
        material.setMat4("_MVP", RenderSystem.calculateMvpMatrix(entity.transform))

        mesh.draw()
    }

    private fun rebuildMesh() {
        if (text.isEmpty()) {
            mesh.setData(ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder()), intArrayOf())
            dirty = false
            return
        }

        val printableCharCount = text.count { char -> char != '\n' && char != ' ' && font.glyphs.containsKey(char) }

        if (printableCharCount == 0) {
            mesh.setData(ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder()), intArrayOf())
            dirty = false
            return
        }

        val vertexCount = printableCharCount * 4
        val indexCount = printableCharCount * 6

        val totalBytes = vertexCount * layout.stride
        val vertexBuffer = ByteBuffer.allocateDirect(totalBytes).order(ByteOrder.nativeOrder())
        val indices = IntArray(indexCount)

        var cursorX = 0f
        var cursorY = 0f

        var vertexOffset = 0
        var indexOffset = 0

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

            // Top-Left
            Vertex(vertexBuffer, (vertexOffset + 0) * layout.stride, layout)
                .setAttributes(Vec3(x, y, 0.0f), Vec2(uvMin.x, uvMax.y))

            // Top-Right
            Vertex(vertexBuffer, (vertexOffset + 1) * layout.stride, layout)
                .setAttributes(Vec3(x + w, y, 0.0f), Vec2(uvMax.x, uvMax.y))

            // Bottom-Right
            Vertex(vertexBuffer, (vertexOffset + 2) * layout.stride, layout)
                .setAttributes(Vec3(x + w, y - h, 0.0f), Vec2(uvMax.x, uvMin.y))

            // Bottom-Left
            Vertex(vertexBuffer, (vertexOffset + 3) * layout.stride, layout)
                .setAttributes(Vec3(x, y - h, 0.0f), Vec2(uvMin.x, uvMin.y))

            indices[indexOffset + 0] = vertexOffset + 0
            indices[indexOffset + 1] = vertexOffset + 1
            indices[indexOffset + 2] = vertexOffset + 2

            indices[indexOffset + 3] = vertexOffset + 2
            indices[indexOffset + 4] = vertexOffset + 3
            indices[indexOffset + 5] = vertexOffset + 0

            vertexOffset += 4
            indexOffset += 6
            cursorX += glyph.advance
        }

        vertexBuffer.rewind()

        mesh.setData(vertexBuffer, indices)
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