package engine.utils

data class Color(val r: Float, val g: Float, val b: Float, val a: Float = 1.0f) {
    companion object {
        val white = Color(1f, 1f, 1f)
        val black = Color(0f, 0f, 0f)
        val red = Color(1f, 0f, 0f)
        val green = Color(0f, 1f, 0f)
        val blue = Color(0f, 0f, 1f)
        val cyan = Color(0f, 1f, 1f)
        val magenta = Color(1f, 0f, 1f)
        val yellow = Color(1f, 1f, 0f)
        val transparent = Color(0f, 0f, 0f, 0f)
        val halfTransparent = Color(0f, 0f, 0f, 0.5f)
        val coolPurple = Color(0.0667f, 0.0f, 0.0902f)
    }
}