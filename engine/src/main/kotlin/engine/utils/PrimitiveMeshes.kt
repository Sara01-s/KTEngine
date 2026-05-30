package engine.utils

import engine.assets.DefaultAssets
import engine.rendering.bindables.Mesh
import engine.rendering.bindables.VertexLayout
import glm_.vec2.Vec2
import glm_.vec3.Vec3

object PrimitiveMeshes : AutoCloseable {
    private val material = DefaultAssets.material

    private val layout = VertexLayout()
        .append(VertexLayout.ElementType.Position3D)
        .append(VertexLayout.ElementType.Texture2D)

    val empty: Mesh by lazy {
        val vertices = listOf(Vec3.zero)
        val indices = intArrayOf(0)
        Mesh(layout, vertices, indices, material)
    }

    val quad: Mesh by lazy {
        val vertices = listOf(
            Vec3(-0.5f, -0.5f, 0f), Vec2(0f, 0f),
            Vec3( 0.5f, -0.5f, 0f), Vec2(1f, 0f),
            Vec3( 0.5f,  0.5f, 0f), Vec2(1f, 1f),
            Vec3(-0.5f,  0.5f, 0f), Vec2(0f, 1f)
        )

        val indices = intArrayOf(
            0, 1, 2,
            2, 3, 0
        )

        Mesh(layout, vertices, indices, material)
    }

    val plane: Mesh by lazy {
        val size = 5f

        val vertices = listOf(
            Vec3(-size, 0f, -size), Vec2(0f, 0f),
            Vec3( size, 0f, -size), Vec2(1f, 0f),
            Vec3( size, 0f,  size), Vec2(1f, 1f),
            Vec3(-size, 0f,  size), Vec2(0f, 1f)
        )

        val indices = intArrayOf(
            0, 1, 2,
            2, 3, 0
        )

        Mesh(layout, vertices, indices, material)
    }

    val cube: Mesh by lazy {
        val vertices = listOf(
            // Front (+Z)
            Vec3(-0.5f, -0.5f,  0.5f), Vec2(0f, 0f),
            Vec3( 0.5f, -0.5f,  0.5f), Vec2(1f, 0f),
            Vec3( 0.5f,  0.5f,  0.5f), Vec2(1f, 1f),
            Vec3(-0.5f,  0.5f,  0.5f), Vec2(0f, 1f),

            // Back (-Z)
            Vec3( 0.5f, -0.5f, -0.5f), Vec2(0f, 0f),
            Vec3(-0.5f, -0.5f, -0.5f), Vec2(1f, 0f),
            Vec3(-0.5f,  0.5f, -0.5f), Vec2(1f, 1f),
            Vec3( 0.5f,  0.5f, -0.5f), Vec2(0f, 1f),

            // Left (-X)
            Vec3(-0.5f, -0.5f, -0.5f), Vec2(0f, 0f),
            Vec3(-0.5f, -0.5f,  0.5f), Vec2(1f, 0f),
            Vec3(-0.5f,  0.5f,  0.5f), Vec2(1f, 1f),
            Vec3(-0.5f,  0.5f, -0.5f), Vec2(0f, 1f),

            // Right (+X)
            Vec3( 0.5f, -0.5f,  0.5f), Vec2(0f, 0f),
            Vec3( 0.5f, -0.5f, -0.5f), Vec2(1f, 0f),
            Vec3( 0.5f,  0.5f, -0.5f), Vec2(1f, 1f),
            Vec3( 0.5f,  0.5f,  0.5f), Vec2(0f, 1f),

            // Top (+Y)
            Vec3(-0.5f,  0.5f,  0.5f), Vec2(0f, 0f),
            Vec3( 0.5f,  0.5f,  0.5f), Vec2(1f, 0f),
            Vec3( 0.5f,  0.5f, -0.5f), Vec2(1f, 1f),
            Vec3(-0.5f,  0.5f, -0.5f), Vec2(0f, 1f),

            // Bottom (-Y)
            Vec3(-0.5f, -0.5f, -0.5f), Vec2(0f, 0f),
            Vec3( 0.5f, -0.5f, -0.5f), Vec2(1f, 0f),
            Vec3( 0.5f, -0.5f,  0.5f), Vec2(1f, 1f),
            Vec3(-0.5f, -0.5f,  0.5f), Vec2(0f, 1f)
        )

        val indices = intArrayOf(
            // Front
            0, 1, 2,
            2, 3, 0,

            // Back
            4, 5, 6,
            6, 7, 4,

            // Left
            8, 9, 10,
            10, 11, 8,

            // Right
            12, 13, 14,
            14, 15, 12,

            // Top
            16, 17, 18,
            18, 19, 16,

            // Bottom
            20, 21, 22,
            22, 23, 20
        )

        Mesh(layout, vertices, indices, material)
    }

    override fun close() {
        quad.close()
        cube.close()
        plane.close()
    }
}