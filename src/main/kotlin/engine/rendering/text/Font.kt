package engine.rendering.text

import com.google.gson.JsonParser
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
    var lineHeight: Float,
) {
    companion object {
        internal fun loadFont(texture: Texture, structure: String): Font {
            val json = JsonParser.parseString(structure).asJsonObject

            val common = json.getAsJsonObject("common")
            val chars = json.getAsJsonArray("chars")

            val atlasWidth = common["scaleW"].asFloat
            val atlasHeight = common["scaleH"].asFloat

            val lineHeight = common["lineHeight"].asFloat

            val glyphs = mutableMapOf<Char, Glyph>()

            for (glyphElement in chars) {
                val glyphJson = glyphElement.asJsonObject

                val character = glyphJson["char"].asString.first()

                val x = glyphJson["x"].asFloat
                val y = glyphJson["y"].asFloat

                val width = glyphJson["width"].asFloat
                val height = glyphJson["height"].asFloat

                val xOffset = glyphJson["xoffset"].asFloat
                val yOffset = glyphJson["yoffset"].asFloat

                val xAdvance = glyphJson["xadvance"].asFloat

                val uvMin = Vec2(
                    x / atlasWidth,
                    1f - (y + height) / atlasHeight
                )

                val uvMax = Vec2(
                    (x + width) / atlasWidth,
                    1f - y / atlasHeight
                )

                val size = Vec2(width / lineHeight, height / lineHeight)
                val offset = Vec2(xOffset / lineHeight, yOffset / lineHeight)

                glyphs[character] = Glyph(
                    character = character,
                    uvMin = uvMin,
                    uvMax = uvMax,
                    size = size,
                    offset = offset,
                    advance = xAdvance / lineHeight
                )
            }

            return Font(
                texture = texture,
                glyphs = glyphs,
                lineHeight = 1f
            )
        }
    }
}