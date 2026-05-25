package engine.rendering.bindables

import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import java.nio.ByteBuffer
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

        fun read(buffer: ByteBuffer, offset: Int): Any = when (typeClass) {
            Vec2::class -> Vec2(buffer.getFloat(offset), buffer.getFloat(offset + 4))
            Vec3::class -> Vec3(buffer.getFloat(offset), buffer.getFloat(offset + 4), buffer.getFloat(offset + 8))
            Vec4::class -> Vec4(buffer.getFloat(offset), buffer.getFloat(offset + 4), buffer.getFloat(offset + 8), buffer.getFloat(offset + 12))
            Int::class  -> buffer.getInt(offset)
            else -> error("Unsupported type: ${typeClass.simpleName}")
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