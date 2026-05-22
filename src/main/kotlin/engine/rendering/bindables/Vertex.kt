package engine.rendering.bindables

import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import java.nio.ByteBuffer

class Vertex(val buffer: ByteBuffer, val baseOffset: Int, val layout: VertexLayout) {

    inline fun <reified T : Any> getAttribute(type: VertexLayout.ElementType): T {
        val element = layout.elements.find { it.type == type }
            ?: error("Vertex::GetAttribute: Element type not found in layout")

        val absoluteOffset = baseOffset + element.offset

        return when (T::class) {
            Vec3::class -> Vec3(
                buffer.getFloat(absoluteOffset),
                buffer.getFloat(absoluteOffset + 4),
                buffer.getFloat(absoluteOffset + 8)
            ) as T
            Vec2::class -> Vec2(
                buffer.getFloat(absoluteOffset),
                buffer.getFloat(absoluteOffset + 4)
            ) as T
            Vec4::class -> Vec4(
                buffer.getFloat(absoluteOffset),
                buffer.getFloat(absoluteOffset + 4),
                buffer.getFloat(absoluteOffset + 8),
                buffer.getFloat(absoluteOffset + 12)
            ) as T
            Int::class -> buffer.getInt(absoluteOffset) as T
            else -> error("Vertex::GetAttribute: Unsupported system type: ${T::class.simpleName}")
        }
    }

    fun setAttributeByIndex(index: Int, value: Any) {
        val element = layout.resolveByIndex(index)
        val absoluteOffset = baseOffset + element.offset

        when (element.type) {
            VertexLayout.ElementType.Position3D,
            VertexLayout.ElementType.Normal3D,
            VertexLayout.ElementType.Tangent3D,
            VertexLayout.ElementType.Bitangent3D,
            VertexLayout.ElementType.ColorFloat3 -> {
                val v = value as? Vec3 ?: error("Type mismatch at index $index. Expected Vec3.")
                buffer.putFloat(absoluteOffset, v.x)
                buffer.putFloat(absoluteOffset + 4, v.y)
                buffer.putFloat(absoluteOffset + 8, v.z)
            }
            VertexLayout.ElementType.Position2D,
            VertexLayout.ElementType.Texture2D -> {
                val v = value as? Vec2 ?: error("Type mismatch at index $index. Expected Vec2.")
                buffer.putFloat(absoluteOffset, v.x)
                buffer.putFloat(absoluteOffset + 4, v.y)
            }
            VertexLayout.ElementType.ColorFloat4 -> {
                val v = value as? Vec4 ?: error("Type mismatch at index $index. Expected Vec4.")
                buffer.putFloat(absoluteOffset, v.x)
                buffer.putFloat(absoluteOffset + 4, v.y)
                buffer.putFloat(absoluteOffset + 8, v.z)
                buffer.putFloat(absoluteOffset + 12, v.w)
            }
        }
    }

    fun setAttributes(vararg attributes: Any) {
        for ((index, attr) in attributes.withIndex()) {
            setAttributeByIndex(index, attr)
        }
    }
}