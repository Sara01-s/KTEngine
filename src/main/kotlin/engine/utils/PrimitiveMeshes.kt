package engine.utils

import engine.rendering.bindables.Mesh
import engine.rendering.bindables.Vertex
import engine.rendering.bindables.VertexLayout
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import java.nio.ByteBuffer
import java.nio.ByteOrder

object PrimitiveMeshes : AutoCloseable {
    val layout = VertexLayout()
        .append(VertexLayout.ElementType.Position3D)
        .append(VertexLayout.ElementType.Texture2D)

    val quad: Mesh
    val cube: Mesh

    init {
        val quadVerticesData = listOf(
            Vec3(-0.5f, -0.5f, 0.0f) to Vec2(0f, 0f),
            Vec3( 0.5f, -0.5f, 0.0f) to Vec2(1f, 0f),
            Vec3( 0.5f,  0.5f, 0.0f) to Vec2(1f, 1f),
            Vec3(-0.5f,  0.5f, 0.0f) to Vec2(0f, 1f)
        )

        val quadIndices = intArrayOf(0, 1, 2, 2, 3, 0)

        quad = Mesh(
            vertexBuffer = createDirectVertexBuffer(quadVerticesData),
            indices = quadIndices,
            layout = layout
        )

        val cubeVerticesData = listOf(
            // --- FRONT FACE (Z = 0.5f) ---
            Vec3(-0.5f, -0.5f,  0.5f) to Vec2(0f, 0f), // 0
            Vec3( 0.5f, -0.5f,  0.5f) to Vec2(1f, 0f), // 1
            Vec3( 0.5f,  0.5f,  0.5f) to Vec2(1f, 1f), // 2
            Vec3(-0.5f,  0.5f,  0.5f) to Vec2(0f, 1f), // 3

            // --- BACK FACE (Z = -0.5f) ---
            Vec3( 0.5f, -0.5f, -0.5f) to Vec2(0f, 0f), // 4
            Vec3(-0.5f, -0.5f, -0.5f) to Vec2(1f, 0f), // 5
            Vec3(-0.5f,  0.5f, -0.5f) to Vec2(1f, 1f), // 6
            Vec3( 0.5f,  0.5f, -0.5f) to Vec2(0f, 1f), // 7

            // --- TOP FACE (Y = 0.5f) ---
            Vec3(-0.5f,  0.5f,  0.5f) to Vec2(0f, 0f), // 8
            Vec3( 0.5f,  0.5f,  0.5f) to Vec2(1f, 0f), // 9
            Vec3( 0.5f,  0.5f, -0.5f) to Vec2(1f, 1f), // 10
            Vec3(-0.5f,  0.5f, -0.5f) to Vec2(0f, 1f), // 11

            // --- BOTTOM FACE (Y = -0.5f) ---
            Vec3(-0.5f, -0.5f, -0.5f) to Vec2(0f, 0f), // 12
            Vec3( 0.5f, -0.5f, -0.5f) to Vec2(1f, 0f), // 13
            Vec3( 0.5f, -0.5f,  0.5f) to Vec2(1f, 1f), // 14
            Vec3(-0.5f, -0.5f,  0.5f) to Vec2(0f, 1f), // 15

            // --- RIGHT FACE (X = 0.5f) ---
            Vec3( 0.5f, -0.5f,  0.5f) to Vec2(0f, 0f), // 16
            Vec3( 0.5f, -0.5f, -0.5f) to Vec2(1f, 0f), // 17
            Vec3( 0.5f,  0.5f, -0.5f) to Vec2(1f, 1f), // 18
            Vec3( 0.5f,  0.5f,  0.5f) to Vec2(0f, 1f), // 19

            // --- LEFT FACE (X = -0.5f) ---
            Vec3(-0.5f, -0.5f, -0.5f) to Vec2(0f, 0f), // 20
            Vec3(-0.5f, -0.5f,  0.5f) to Vec2(1f, 0f), // 21
            Vec3(-0.5f,  0.5f,  0.5f) to Vec2(1f, 1f), // 22
            Vec3(-0.5f,  0.5f, -0.5f) to Vec2(0f, 1f), // 23
        )

        val cubeIndices = intArrayOf(
            0,  1,  2,    2,  3,  0,  // Front face.
            4,  5,  6,    6,  7,  4,  // Back face.
            8,  9,  10,   10, 11, 8,  // Top face.
            12, 13, 14,   14, 15, 12, // Bottom face.
            16, 17, 18,   18, 19, 16, // Right face.
            20, 21, 22,   22, 23, 20  // Left face.
        )

        cube = Mesh(
            vertexBuffer = createDirectVertexBuffer(cubeVerticesData),
            indices = cubeIndices,
            layout = layout
        )
    }

    private fun createDirectVertexBuffer(data: List<Pair<Vec3, Vec2>>): ByteBuffer {
        val totalBytes = data.size * layout.stride
        val buffer = ByteBuffer.allocateDirect(totalBytes).order(ByteOrder.nativeOrder())

        for ((index, vertexData) in data.withIndex()) {
            val baseOffset = index * layout.stride
            val vertexView = Vertex(buffer, baseOffset, layout)

            vertexView.setAttributes(vertexData.first, vertexData.second)
        }

        buffer.rewind()
        return buffer
    }

    override fun close() {
        quad.close()
        cube.close()
    }
}