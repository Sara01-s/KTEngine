package engine.utils

import engine.rendering.bindables.Mesh

object PrimitiveMeshes : AutoCloseable{
    val quad = Mesh(
        vertices = floatArrayOf(
            /*pos*/ -0.5f, -0.5f, /*uv*/ 0f, 0f,
            /*pos*/  0.5f, -0.5f, /*uv*/ 1f, 0f,
            /*pos*/  0.5f,  0.5f, /*uv*/ 1f, 1f,
            /*pos*/ -0.5f,  0.5f, /*uv*/ 0f, 1f
        ),
        indices = intArrayOf(0, 1, 2, 2, 3, 0)
    )

    override fun close() {
        quad.close()
    }
}