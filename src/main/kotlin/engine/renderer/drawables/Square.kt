package engine.renderer.drawables

import engine.game.Assets
import engine.renderer.Color
import engine.renderer.bindables.*
import glm_.vec2.Vec2
import org.lwjgl.opengl.GL11.*

class Square(
    shader: Shader = Assets.loadDefaultShader(),
    vertexColor: Color = Color.white
) : Drawable(shader) {

    init {
        val vertices = arrayOf(
            Vertex(Vec2(-0.5f, -0.5f), Vec2(0f, 0f), vertexColor),
            Vertex(Vec2( 0.5f, -0.5f), Vec2(1f, 0f), vertexColor),
            Vertex(Vec2( 0.5f,  0.5f), Vec2(1f, 1f), vertexColor),
            Vertex(Vec2(-0.5f,  0.5f), Vec2(0f, 1f), vertexColor)
        )

        val indices = intArrayOf(
            0, 1, 2,
            2, 3, 0
        )

        val vao = VertexArrayObject()
        val vbo = VertexBuffer(vertices.toByteBuffer())
        val ibo = IndexBuffer(indices.toIntBuffet())

        vao.addAttribute(0, GL_FLOAT, 2, Vertex.SIZE_BYTES, 0L)
        vao.addAttribute(1, GL_FLOAT, 2, Vertex.SIZE_BYTES, (2 * Float.SIZE_BYTES).toLong())
        vao.addAttribute(2, GL_FLOAT, 4, Vertex.SIZE_BYTES, (4 * Float.SIZE_BYTES).toLong())

        setBindables(vao, vbo, ibo)

        drawCommand = DrawCommand(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0L)
    }
}