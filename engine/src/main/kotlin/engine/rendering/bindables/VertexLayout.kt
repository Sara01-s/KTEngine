package engine.rendering.bindables

import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import java.nio.ByteBuffer
import kotlin.reflect.KClass

class VertexLayout {

    enum class ElementType(val location: Int, val length: Int, val glType: Int, val typeClass: KClass<*>) {
        Position3D(0, 3, GL_FLOAT, Vec3::class),
        Normal3D(1, 3, GL_FLOAT, Vec3::class),
        Texture2D(2, 2, GL_FLOAT, Vec2::class),
        Tangent3D(3, 3, GL_FLOAT, Vec3::class),
        Bitangent3D(4, 3, GL_FLOAT, Vec3::class),
        Position2D(0, 2, GL_FLOAT, Vec2::class),
        ColorFloat3(5, 3, GL_FLOAT, Vec3::class),
        ColorFloat4(5, 4, GL_FLOAT, Vec4::class);

        val sizeInBytes: Int get() = when (glType) {
            GL_FLOAT -> length * Float.SIZE_BYTES
            GL_UNSIGNED_BYTE -> length
            else -> 0
        }

        fun write(buffer: ByteBuffer, offset: Int, value: Any) {
            require(typeClass.isInstance(value)) {
                "Type mismatch for $name: expected ${typeClass.simpleName}, got ${value::class.simpleName}"
            }
            when (value) {
                is Vec2 -> { buffer.putFloat(offset, value.x); buffer.putFloat(offset + 4, value.y) }
                is Vec3 -> { buffer.putFloat(offset, value.x); buffer.putFloat(offset + 4, value.y); buffer.putFloat(offset + 8, value.z) }
                is Vec4 -> { buffer.putFloat(offset, value.x); buffer.putFloat(offset + 4, value.y); buffer.putFloat(offset + 8, value.z); buffer.putFloat(offset + 12, value.w) }
                is Int  -> buffer.putInt(offset, value)
            }
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
}