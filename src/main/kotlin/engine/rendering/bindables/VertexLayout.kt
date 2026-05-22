package engine.rendering.bindables

import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import kotlin.reflect.KClass

class VertexLayout {

    enum class ElementType(val length: Int, val glType: Int, val typeClass: KClass<*>) {
        Position2D(2, GL_FLOAT, Vec2::class),
        Position3D(3, GL_FLOAT, Vec3::class),
        Texture2D(2, GL_FLOAT, Vec2::class),
        Normal3D(3, GL_FLOAT, Vec3::class),
        Tangent3D(3, GL_FLOAT, Vec3::class),
        Bitangent3D(3, GL_FLOAT, Vec3::class),
        ColorFloat3(3, GL_FLOAT, Vec3::class),
        ColorFloat4(4, GL_FLOAT, Vec4::class);

        val sizeInBytes: Int
            get() = when (glType) {
                GL_FLOAT -> length * Float.SIZE_BYTES
                GL_UNSIGNED_BYTE -> length * 1
                else -> 0
            }
    }

    data class Element(val type: ElementType, val offset: Int)

    val elements = mutableListOf<Element>()

    val stride: Int
        get() = elements.lastOrNull()?.let { it.offset + it.type.sizeInBytes } ?: 0

    fun append(type: ElementType): VertexLayout {
        val currentOffset = stride
        elements.add(Element(type, currentOffset))

        return this
    }

    fun resolveByIndex(index: Int): Element {
        return elements[index]
    }
}