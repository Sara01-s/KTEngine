package engine.scenes

import engine.rendering.text.TextRenderer

class MainMenuScene : Scene() {
    private val texto = createEntity().apply {
        addComponent<TextRenderer>()
    }
}

