package engine.utils

import engine.rendering.bindables.Mesh
import engine.rendering.bindables.VertexLayout
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import kotlin.math.*

object PrimitiveMeshes : AutoCloseable {
    val layout = VertexLayout()
        .append(VertexLayout.ElementType.Position3D)
        .append(VertexLayout.ElementType.Texture2D)

    val quad: Mesh
    val cube: Mesh
    val sphere: Mesh
    val plane: Mesh
    val cylinder: Mesh
    val capsule: Mesh

    init {
        quad     = createQuad()
        cube     = createCube()
        sphere   = createSphere()
        plane    = createPlane()
        cylinder = createCylinder()
        capsule  = createCapsule()
    }

    fun createQuad(): Mesh {
        val vertices = listOf(
            Vec3(-0.5f, -0.5f, 0.0f), Vec2(0f, 0f),
            Vec3( 0.5f, -0.5f, 0.0f), Vec2(1f, 0f),
            Vec3( 0.5f,  0.5f, 0.0f), Vec2(1f, 1f),
            Vec3(-0.5f,  0.5f, 0.0f), Vec2(0f, 1f)
        )
        val indices = intArrayOf(0, 1, 2, 2, 3, 0)
        return Mesh(layout, vertices, indices)
    }

    fun createCube(): Mesh {
        val vertices = listOf(
            // --- FRONT FACE (Z = 0.5f) ---
            Vec3(-0.5f, -0.5f,  0.5f), Vec2(0f, 0f),
            Vec3( 0.5f, -0.5f,  0.5f), Vec2(1f, 0f),
            Vec3( 0.5f,  0.5f,  0.5f), Vec2(1f, 1f),
            Vec3(-0.5f,  0.5f,  0.5f), Vec2(0f, 1f),
            // --- BACK FACE (Z = -0.5f) ---
            Vec3( 0.5f, -0.5f, -0.5f), Vec2(0f, 0f),
            Vec3(-0.5f, -0.5f, -0.5f), Vec2(1f, 0f),
            Vec3(-0.5f,  0.5f, -0.5f), Vec2(1f, 1f),
            Vec3( 0.5f,  0.5f, -0.5f), Vec2(0f, 1f),
            // --- TOP FACE (Y = 0.5f) ---
            Vec3(-0.5f,  0.5f,  0.5f), Vec2(0f, 0f),
            Vec3( 0.5f,  0.5f,  0.5f), Vec2(1f, 0f),
            Vec3( 0.5f,  0.5f, -0.5f), Vec2(1f, 1f),
            Vec3(-0.5f,  0.5f, -0.5f), Vec2(0f, 1f),
            // --- BOTTOM FACE (Y = -0.5f) ---
            Vec3(-0.5f, -0.5f, -0.5f), Vec2(0f, 0f),
            Vec3( 0.5f, -0.5f, -0.5f), Vec2(1f, 0f),
            Vec3( 0.5f, -0.5f,  0.5f), Vec2(1f, 1f),
            Vec3(-0.5f, -0.5f,  0.5f), Vec2(0f, 1f),
            // --- RIGHT FACE (X = 0.5f) ---
            Vec3( 0.5f, -0.5f,  0.5f), Vec2(0f, 0f),
            Vec3( 0.5f, -0.5f, -0.5f), Vec2(1f, 0f),
            Vec3( 0.5f,  0.5f, -0.5f), Vec2(1f, 1f),
            Vec3( 0.5f,  0.5f,  0.5f), Vec2(0f, 1f),
            // --- LEFT FACE (X = -0.5f) ---
            Vec3(-0.5f, -0.5f, -0.5f), Vec2(0f, 0f),
            Vec3(-0.5f, -0.5f,  0.5f), Vec2(1f, 0f),
            Vec3(-0.5f,  0.5f,  0.5f), Vec2(1f, 1f),
            Vec3(-0.5f,  0.5f, -0.5f), Vec2(0f, 1f),
        )
        val indices = intArrayOf(
            0,  2,  1,    2,  0,  3,
            4,  6,  5,    6,  4,  7,
            8,  10, 9,    10, 8,  11,
            12, 14, 13,   14, 12, 15,
            16, 18, 17,   18, 16, 19,
            20, 22, 21,   22, 20, 23
        )
        return Mesh(layout, vertices, indices)
    }

    /**
     * UV sphere centered at origin.
     * @param radius Sphere radius.
     * @param stacks Horizontal rings (latitude divisions). Min 2.
     * @param slices Vertical segments (longitude divisions). Min 3.
     */
    fun createSphere(radius: Float = 0.5f, stacks: Int = 16, slices: Int = 32): Mesh {
        val vertices = mutableListOf<Any>()
        val indices = mutableListOf<Int>()

        for (i in 0..stacks) {
            val phi = PI.toFloat() * i / stacks
            val y = radius * cos(phi)
            val r = radius * sin(phi)
            val v = i.toFloat() / stacks
            for (j in 0..slices) {
                val theta = 2f * PI.toFloat() * j / slices
                val x = r * cos(theta)
                val z = r * sin(theta)
                vertices += Vec3(x, y, z)
                vertices += Vec2(j.toFloat() / slices, v)
            }
        }

        val ring = slices + 1
        for (i in 0 until stacks) {
            for (j in 0 until slices) {
                val a = i * ring + j
                val b = a + ring
                indices += intArrayOf(a, b, a + 1, b, b + 1, a + 1).toList()
            }
        }

        return Mesh(layout, vertices, indices.toIntArray())
    }

    /**
     * Flat XZ plane centered at origin, facing +Y.
     * @param width      Size along X axis.
     * @param depth      Size along Z axis.
     * @param segmentsX  Subdivisions along X. Min 1.
     * @param segmentsZ  Subdivisions along Z. Min 1.
     */
    fun createPlane(
        width: Float = 1f,
        depth: Float = 1f,
        segmentsX: Int = 1,
        segmentsZ: Int = 1
    ): Mesh {
        val vertices = mutableListOf<Any>()
        val indices = mutableListOf<Int>()
        val halfW = width / 2f
        val halfD = depth / 2f

        for (iz in 0..segmentsZ) {
            val z = -halfD + depth * iz / segmentsZ
            val v = iz.toFloat() / segmentsZ
            for (ix in 0..segmentsX) {
                val x = -halfW + width * ix / segmentsX
                vertices += Vec3(x, 0f, z)
                vertices += Vec2(ix.toFloat() / segmentsX, v)
            }
        }

        val cols = segmentsX + 1
        for (iz in 0 until segmentsZ) {
            for (ix in 0 until segmentsX) {
                val a = iz * cols + ix
                val b = a + cols
                indices += intArrayOf(a, b, a + 1, b, b + 1, a + 1).toList()
            }
        }

        return Mesh(layout, vertices, indices.toIntArray())
    }

    /**
     * Open or capped cylinder along the Y axis, centered at origin.
     * @param radius     Radius of the cylinder.
     * @param height     Total height.
     * @param slices     Circumference subdivisions. Min 3.
     * @param stacks     Height subdivisions for the body. Min 1.
     * @param capTop     Whether to generate a top disk cap.
     * @param capBottom  Whether to generate a bottom disk cap.
     */
    fun createCylinder(
        radius: Float = 0.5f,
        height: Float = 1f,
        slices: Int = 32,
        stacks: Int = 1,
        capTop: Boolean = true,
        capBottom: Boolean = true
    ): Mesh {
        val vertices = mutableListOf<Any>()
        val indices = mutableListOf<Int>()
        val halfH = height / 2f

        // Body
        for (i in 0..stacks) {
            val y = -halfH + height * i / stacks
            val v = i.toFloat() / stacks
            for (j in 0..slices) {
                val theta = 2f * PI.toFloat() * j / slices
                vertices += Vec3(radius * cos(theta), y, radius * sin(theta))
                vertices += Vec2(j.toFloat() / slices, v)
            }
        }

        val ring = slices + 1
        for (i in 0 until stacks) {
            for (j in 0 until slices) {
                val a = i * ring + j
                val b = a + ring
                indices += intArrayOf(a, b, a + 1, b, b + 1, a + 1).toList()
            }
        }

        // Caps
        fun addCap(y: Float, flip: Boolean) {
            val centerIdx = vertices.size / 2
            vertices += Vec3(0f, y, 0f)
            vertices += Vec2(0.5f, 0.5f)
            val startIdx = centerIdx + 1
            for (j in 0..slices) {
                val theta = 2f * PI.toFloat() * j / slices
                vertices += Vec3(radius * cos(theta), y, radius * sin(theta))
                vertices += Vec2(0.5f + 0.5f * cos(theta), 0.5f + 0.5f * sin(theta))
            }
            for (j in 0 until slices) {
                val a = startIdx + j
                val b = startIdx + j + 1
                if (flip) indices += intArrayOf(centerIdx, b, a).toList()
                else      indices += intArrayOf(centerIdx, a, b).toList()
            }
        }

        if (capBottom) addCap(-halfH, flip = false)
        if (capTop)    addCap( halfH, flip = true)

        return Mesh(layout, vertices, indices.toIntArray())
    }

    /**
     * Cápsula (cilindro + tapas hemisféricas) alineada en el eje Y, centrada en el origen.
     * @param radius           Radio de las esferas y del cuerpo cilíndrico.
     * @param height           Altura total (incluyendo ambas tapas).
     * @param slices           Subdivisiones alrededor de la circunferencia (mínimo 3).
     * @param hemisphereStacks Anillos de latitud por cada hemisferio (mínimo 1).
     * @param bodyStacks       Subdivisiones de altura para el cuerpo del cilindro (mínimo 1).
     */
    fun createCapsule(
        radius: Float = 0.5f,
        height: Float = 2f,
        slices: Int = 32,
        hemisphereStacks: Int = 8,
        bodyStacks: Int = 1
    ): Mesh {
        val vertices = mutableListOf<Any>()
        val indices = mutableListOf<Int>()

        // Evitar alturas absurdas menores que el propio diámetro de las tapas
        val bodyHeight = (height - 2f * radius).coerceAtLeast(0f)
        val halfBody = bodyHeight / 2f
        val ringVerticesCount = slices + 1

        // Número total de anillos que vamos a generar
        val totalRings = (hemisphereStacks * 2) + bodyStacks + 1
        var currentRing = 0

        // Helper para añadir un anillo de vértices
        fun addRing(y: Float, vCoord: Float, xzScale: Float = 1f) {
            for (j in 0..slices) {
                val theta = 2f * PI.toFloat() * j / slices

                // Posición 3D
                val x = radius * xzScale * cos(theta)
                val z = radius * xzScale * sin(theta)
                vertices += Vec3(x, y, z)

                // Coordenadas de textura (U, V)
                val u = j.toFloat() / slices
                vertices += Vec2(u, vCoord)
            }
        }

        // 1. Hemisferio Superior (de arriba hacia la base del domo)
        for (i in 0..hemisphereStacks) {
            // phi va desde PI/2 (polo norte) hasta 0 (ecuador superior del cilindro)
            val phi = (PI / 2.0 * (hemisphereStacks - i) / hemisphereStacks).toFloat()
            val vCoord = (hemisphereStacks - i).toFloat() / (totalRings - 1)

            val y = halfBody + radius * sin(phi)
            val scale = cos(phi)
            addRing(y, vCoord, scale)
            currentRing++
        }

        // 2. Cuerpo Cilíndrico
        for (i in 1..bodyStacks) {
            val factor = i.toFloat() / bodyStacks
            val y = halfBody - bodyHeight * factor
            val vCoord = (hemisphereStacks + i).toFloat() / (totalRings - 1)

            addRing(y, vCoord, 1f)
            currentRing++
        }

        // 3. Hemisferio Inferior (del ecuador inferior al polo sur)
        for (i in 1..hemisphereStacks) {
            // phi va desde 0 hasta PI/2 para el domo invertido
            val phi = (PI / 2.0 * i / hemisphereStacks).toFloat()
            val vCoord = (hemisphereStacks + bodyStacks + i).toFloat() / (totalRings - 1)

            val y = -halfBody - radius * sin(phi)
            val scale = cos(phi)
            addRing(y, vCoord, scale)
            currentRing++
        }

        // 4. Costura de Índices (Generación de Triángulos)
        // Corregido: 'ringIdx' representa el índice base del anillo completo en el búfer
        for (i in 0 until totalRings - 1) {
            val topRingBase = i * ringVerticesCount
            val botRingBase = (i + 1) * ringVerticesCount

            for (j in 0 until slices) {
                val topLeft  = topRingBase + j
                val topRight = topLeft + 1
                val botLeft  = botRingBase + j
                val botRight = botLeft + 1

                // Triángulo 1
                indices += topLeft
                indices += botLeft
                indices += topRight

                // Triángulo 2
                indices += botLeft
                indices += botRight
                indices += topRight
            }
        }

        return Mesh(layout, vertices, indices.toIntArray())
    }

    override fun close() {
        quad.close()
        cube.close()
        sphere.close()
        plane.close()
        cylinder.close()
        capsule.close()
    }
}