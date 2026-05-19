package engine.rendering.text

import engine.rendering.bindables.Texture
import glm_.vec2.Vec2

data class Glyph(
    val character: Char,

    val uvMin: Vec2,
    val uvMax: Vec2,

    val size: Vec2,
    val offset: Vec2,

    val advance: Float
)

class Font(
    val texture: Texture,
    val glyphs: Map<Char, Glyph>,
    val lineHeight: Float
) {

}