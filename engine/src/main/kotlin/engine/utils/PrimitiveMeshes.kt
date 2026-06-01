package engine.utils

import engine.assets.DefaultAssets
import engine.rendering.bindables.Mesh
import engine.rendering.bindables.VertexLayout
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

object PrimitiveMeshes : AutoCloseable {
    private val material = DefaultAssets.litMaterial

    private val layout = VertexLayout()
        .append(VertexLayout.ElementType.Position3D)
        .append(VertexLayout.ElementType.Normal3D)
        .append(VertexLayout.ElementType.Texture2D)

    val empty: Mesh by lazy {
        val vertices = listOf(Vec3.zero, Vec3.up, Vec2(0f, 0f))
        val indices = intArrayOf(0)
        Mesh(layout, vertices, indices, material)
    }

    val quad: Mesh by lazy {
        val normal = Vec3(0f, 0f, 1f)
        val vertices = listOf(
            Vec3(-1f, -1f, 0f), normal, Vec2(0f, 0f),
            Vec3( 1f, -1f, 0f), normal, Vec2(1f, 0f),
            Vec3( 1f,  1f, 0f), normal, Vec2(1f, 1f),
            Vec3(-1f,  1f, 0f), normal, Vec2(0f, 1f)
        )

        val indices = intArrayOf(0, 1, 2, 2, 3, 0)
        Mesh(layout, vertices, indices, material)
    }

    val plane: Mesh by lazy {
        val size = 5f
        val normal = Vec3(0f, 1f, 0f)
        val vertices = listOf(
            Vec3(-size, 0f, -size), normal, Vec2(0f, 0f),
            Vec3( size, 0f, -size), normal, Vec2(1f, 0f),
            Vec3( size, 0f,  size), normal, Vec2(1f, 1f),
            Vec3(-size, 0f,  size), normal, Vec2(0f, 1f)
        )

        val indices = intArrayOf(0, 2, 1, 2, 0, 3)
        Mesh(layout, vertices, indices, material)
    }

    val sphere: Mesh by lazy {
        val rings = 20
        val sectors = 20
        val radius = 0.5f
        val vertices = mutableListOf<Any>()
        val indices = mutableListOf<Int>()

        for (i in 0..rings) {
            val v = i.toFloat() / rings
            val phi = v * PI.toFloat()
            for (j in 0..sectors) {
                val u = j.toFloat() / sectors
                val theta = u * PI.toFloat() * 2f

                val x = cos(theta) * sin(phi)
                val y = cos(phi)
                val z = sin(theta) * sin(phi)

                val normal = Vec3(x, y, z)
                vertices.add(Vec3(x * radius, y * radius, z * radius))
                vertices.add(normal)
                vertices.add(Vec2(u, v))
            }
        }

        for (i in 0 until rings) {
            for (j in 0 until sectors) {
                val first = (i * (sectors + 1)) + j
                val second = first + sectors + 1
                indices.addAll(listOf(first, second, first + 1, second, second + 1, first + 1))
            }
        }

        Mesh(layout, vertices, indices.toIntArray(), material)
    }

    val cube: Mesh by lazy {
        val vertices = listOf(
            // Front (+Z)
            Vec3(-0.5f, -0.5f,  0.5f), Vec3(0f, 0f, 1f), Vec2(0f, 0f),
            Vec3( 0.5f, -0.5f,  0.5f), Vec3(0f, 0f, 1f), Vec2(1f, 0f),
            Vec3( 0.5f,  0.5f,  0.5f), Vec3(0f, 0f, 1f), Vec2(1f, 1f),
            Vec3(-0.5f,  0.5f,  0.5f), Vec3(0f, 0f, 1f), Vec2(0f, 1f),

            // Back (-Z)
            Vec3( 0.5f, -0.5f, -0.5f), Vec3(0f, 0f, -1f), Vec2(0f, 0f),
            Vec3(-0.5f, -0.5f, -0.5f), Vec3(0f, 0f, -1f), Vec2(1f, 0f),
            Vec3(-0.5f,  0.5f, -0.5f), Vec3(0f, 0f, -1f), Vec2(1f, 1f),
            Vec3( 0.5f,  0.5f, -0.5f), Vec3(0f, 0f, -1f), Vec2(0f, 1f),

            // Left (-X)
            Vec3(-0.5f, -0.5f, -0.5f), Vec3(-1f, 0f, 0f), Vec2(0f, 0f),
            Vec3(-0.5f, -0.5f,  0.5f), Vec3(-1f, 0f, 0f), Vec2(1f, 0f),
            Vec3(-0.5f,  0.5f,  0.5f), Vec3(-1f, 0f, 0f), Vec2(1f, 1f),
            Vec3(-0.5f,  0.5f, -0.5f), Vec3(-1f, 0f, 0f), Vec2(0f, 1f),

            // Right (+X)
            Vec3( 0.5f, -0.5f,  0.5f), Vec3(1f, 0f, 0f), Vec2(0f, 0f),
            Vec3( 0.5f, -0.5f, -0.5f), Vec3(1f, 0f, 0f), Vec2(1f, 0f),
            Vec3( 0.5f,  0.5f, -0.5f), Vec3(1f, 0f, 0f), Vec2(1f, 1f),
            Vec3( 0.5f,  0.5f,  0.5f), Vec3(1f, 0f, 0f), Vec2(0f, 1f),

            // Top (+Y)
            Vec3(-0.5f,  0.5f,  0.5f), Vec3(0f, 1f, 0f), Vec2(0f, 0f),
            Vec3( 0.5f,  0.5f,  0.5f), Vec3(0f, 1f, 0f), Vec2(1f, 0f),
            Vec3( 0.5f,  0.5f, -0.5f), Vec3(0f, 1f, 0f), Vec2(1f, 1f),
            Vec3(-0.5f,  0.5f, -0.5f), Vec3(0f, 1f, 0f), Vec2(0f, 1f),

            // Bottom (-Y)
            Vec3(-0.5f, -0.5f, -0.5f), Vec3(0f, -1f, 0f), Vec2(0f, 0f),
            Vec3( 0.5f, -0.5f, -0.5f), Vec3(0f, -1f, 0f), Vec2(1f, 0f),
            Vec3( 0.5f, -0.5f,  0.5f), Vec3(0f, -1f, 0f), Vec2(1f, 1f),
            Vec3(-0.5f, -0.5f,  0.5f), Vec3(0f, -1f, 0f), Vec2(0f, 1f)
        )

        val indices = intArrayOf(
            0, 1, 2, 2, 3, 0,
            4, 5, 6, 6, 7, 4,
            8, 9, 10, 10, 11, 8,
            12, 13, 14, 14, 15, 12,
            16, 17, 18, 18, 19, 16,
            20, 21, 22, 22, 23, 20
        )

        Mesh(layout, vertices, indices, material)
    }

    override fun close() {
        quad.close()
        cube.close()
        plane.close()
        sphere.close()
    }
}