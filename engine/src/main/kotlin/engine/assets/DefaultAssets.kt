package engine.assets

import engine.rendering.bindables.Material
import engine.rendering.bindables.Shader
import engine.rendering.bindables.Texture
import engine.rendering.text.Font
import engine.utils.Color

object DefaultAssets {
    val shader: Shader by lazy {
        EngineAssets.loadShader("shaders/shd_lit.glsl")
    }

    val texture: Texture by lazy {
        EngineAssets.loadTexture("textures/tex_square.png")
    }

    val whiteTexture: Texture by lazy {
        Texture.createSolid(Color.white)
    }

    val model by lazy {
        EngineAssets.loadModel("models/model_watercolor_bird.glb")
    }

    val material: Material by lazy {
        Material(shader).apply {
            setTexture("_DiffuseTexture", texture)
            setColor4("_Color", Color.white)
        }
    }

    val textMaterial: Material by lazy {
        Material(
            EngineAssets.loadShader("shaders/shd_font.glsl")
        ).apply {
            setColor4("_Color", Color.white)
        }
    }

    val font: Font by lazy {
        EngineAssets.loadFont(
            texturePath = "fonts/font_tex_pixelated.png",
            structurePath = "fonts/font_structure_pixelated.json"
        )
    }
}