package engine.utils

import engine.rendering.bindables.Mesh
import engine.rendering.bindables.VertexLayout
import glm_.vec2.Vec2
import glm_.vec3.Vec3

object PrimitiveMeshes : AutoCloseable {
    val layout = VertexLayout()
        .append(VertexLayout.ElementType.Position3D)
        .append(VertexLayout.ElementType.Texture2D)

    val quad: Mesh
    val cube: Mesh

    init {
        val quadVertices = listOf(
            Vec3(-0.5f, -0.5f, 0.0f), Vec2(0f, 0f),
            Vec3( 0.5f, -0.5f, 0.0f), Vec2(1f, 0f),
            Vec3( 0.5f,  0.5f, 0.0f), Vec2(1f, 1f),
            Vec3(-0.5f,  0.5f, 0.0f), Vec2(0f, 1f)
        )

        val quadIndices = intArrayOf(0, 1, 2, 2, 3, 0)

        quad = Mesh(layout, quadVertices, quadIndices)

        val cubeVertices = listOf(
            // --- FRONT FACE (Z = 0.5f) ---
            Vec3(-0.5f, -0.5f,  0.5f), Vec2(0f, 0f), // 0
            Vec3( 0.5f, -0.5f,  0.5f), Vec2(1f, 0f), // 1
            Vec3( 0.5f,  0.5f,  0.5f), Vec2(1f, 1f), // 2
            Vec3(-0.5f,  0.5f,  0.5f), Vec2(0f, 1f), // 3

            // --- BACK FACE (Z = -0.5f) ---
            Vec3( 0.5f, -0.5f, -0.5f), Vec2(0f, 0f), // 4
            Vec3(-0.5f, -0.5f, -0.5f), Vec2(1f, 0f), // 5
            Vec3(-0.5f,  0.5f, -0.5f), Vec2(1f, 1f), // 6
            Vec3( 0.5f,  0.5f, -0.5f), Vec2(0f, 1f), // 7

            // --- TOP FACE (Y = 0.5f) ---
            Vec3(-0.5f,  0.5f,  0.5f), Vec2(0f, 0f), // 8
            Vec3( 0.5f,  0.5f,  0.5f), Vec2(1f, 0f), // 9
            Vec3( 0.5f,  0.5f, -0.5f), Vec2(1f, 1f), // 10
            Vec3(-0.5f,  0.5f, -0.5f), Vec2(0f, 1f), // 11

            // --- BOTTOM FACE (Y = -0.5f) ---
            Vec3(-0.5f, -0.5f, -0.5f), Vec2(0f, 0f), // 12
            Vec3( 0.5f, -0.5f, -0.5f), Vec2(1f, 0f), // 13
            Vec3( 0.5f, -0.5f,  0.5f), Vec2(1f, 1f), // 14
            Vec3(-0.5f, -0.5f,  0.5f), Vec2(0f, 1f), // 15

            // --- RIGHT FACE (X = 0.5f) ---
            Vec3( 0.5f, -0.5f,  0.5f), Vec2(0f, 0f), // 16
            Vec3( 0.5f, -0.5f, -0.5f), Vec2(1f, 0f), // 17
            Vec3( 0.5f,  0.5f, -0.5f), Vec2(1f, 1f), // 18
            Vec3( 0.5f,  0.5f,  0.5f), Vec2(0f, 1f), // 19

            // --- LEFT FACE (X = -0.5f) ---
            Vec3(-0.5f, -0.5f, -0.5f), Vec2(0f, 0f), // 20
            Vec3(-0.5f, -0.5f,  0.5f), Vec2(1f, 0f), // 21
            Vec3(-0.5f,  0.5f,  0.5f), Vec2(1f, 1f), // 22
            Vec3(-0.5f,  0.5f, -0.5f), Vec2(0f, 1f), // 23
        )

        val cubeIndices = intArrayOf(
            0,  2,  1,    2,  0,  3,  // Front
            4,  6,  5,    6,  4,  7,  // Back
            8,  10, 9,    10, 8,  11, // Top
            12, 14, 13,   14, 12, 15, // Bottom
            16, 18, 17,   18, 16, 19, // Right
            20, 22, 21,   22, 20, 23  // Left
        )

        cube = Mesh(layout, cubeVertices, cubeIndices)
    }

    override fun close() {
        quad.close()
        cube.close()
    }
}