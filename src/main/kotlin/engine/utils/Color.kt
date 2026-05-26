package engine.utils

data class Color(val r: Float, val g: Float, val b: Float, val a: Float = 1.0f) {

    companion object {

        // Basic
        val white = Color(1f, 1f, 1f)
        val black = Color(0f, 0f, 0f)
        val red = Color(1f, 0f, 0f)
        val green = Color(0f, 1f, 0f)
        val blue = Color(0f, 0f, 1f)
        val cyan = Color(0f, 1f, 1f)
        val magenta = Color(1f, 0f, 1f)
        val yellow = Color(1f, 1f, 0f)

        // Transparency
        val transparent = Color(0f, 0f, 0f, 0f)
        val halfTransparent = Color(0f, 0f, 0f, 0.5f)

        // Grayscale
        val gray05 = Color(0.05f, 0.05f, 0.05f)
        val gray10 = Color(0.10f, 0.10f, 0.10f)
        val gray15 = Color(0.15f, 0.15f, 0.15f)
        val gray20 = Color(0.20f, 0.20f, 0.20f)
        val gray25 = Color(0.25f, 0.25f, 0.25f)
        val gray30 = Color(0.30f, 0.30f, 0.30f)
        val gray35 = Color(0.35f, 0.35f, 0.35f)
        val gray40 = Color(0.40f, 0.40f, 0.40f)
        val gray45 = Color(0.45f, 0.45f, 0.45f)
        val gray50 = Color(0.50f, 0.50f, 0.50f)
        val gray55 = Color(0.55f, 0.55f, 0.55f)
        val gray60 = Color(0.60f, 0.60f, 0.60f)
        val gray65 = Color(0.65f, 0.65f, 0.65f)
        val gray70 = Color(0.70f, 0.70f, 0.70f)
        val gray75 = Color(0.75f, 0.75f, 0.75f)
        val gray80 = Color(0.80f, 0.80f, 0.80f)
        val gray85 = Color(0.85f, 0.85f, 0.85f)
        val gray90 = Color(0.90f, 0.90f, 0.90f)
        val gray95 = Color(0.95f, 0.95f, 0.95f)

        // Reds
        val darkRed = Color(0.55f, 0f, 0f)
        val crimson = Color(0.86f, 0.08f, 0.24f)
        val scarlet = Color(1f, 0.14f, 0f)
        val salmon = Color(0.98f, 0.50f, 0.45f)
        val coral = Color(1f, 0.50f, 0.31f)
        val tomato = Color(1f, 0.39f, 0.28f)
        val indianRed = Color(0.80f, 0.36f, 0.36f)

        // Oranges
        val orange = Color(1f, 0.65f, 0f)
        val darkOrange = Color(1f, 0.55f, 0f)
        val amber = Color(1f, 0.75f, 0f)
        val pumpkin = Color(1f, 0.46f, 0.09f)

        // Yellows
        val gold = Color(1f, 0.84f, 0f)
        val lemon = Color(1f, 0.97f, 0f)
        val khaki = Color(0.94f, 0.90f, 0.55f)

        // Greens
        val darkGreen = Color(0f, 0.39f, 0f)
        val lime = Color(0.75f, 1f, 0f)
        val forestGreen = Color(0.13f, 0.55f, 0.13f)
        val emerald = Color(0.31f, 0.78f, 0.47f)
        val mint = Color(0.60f, 1f, 0.60f)
        val olive = Color(0.50f, 0.50f, 0f)
        val seaGreen = Color(0.18f, 0.55f, 0.34f)

        // Cyans / Teals
        val turquoise = Color(0.25f, 0.88f, 0.82f)
        val teal = Color(0f, 0.50f, 0.50f)
        val aqua = Color(0f, 1f, 1f)

        // Blues
        val navy = Color(0f, 0f, 0.50f)
        val royalBlue = Color(0.25f, 0.41f, 0.88f)
        val skyBlue = Color(0.53f, 0.81f, 0.92f)
        val deepSkyBlue = Color(0f, 0.75f, 1f)
        val dodgerBlue = Color(0.12f, 0.56f, 1f)
        val steelBlue = Color(0.27f, 0.51f, 0.71f)
        val midnightBlue = Color(0.10f, 0.10f, 0.44f)

        // Purples
        val purple = Color(0.50f, 0f, 0.50f)
        val violet = Color(0.93f, 0.51f, 0.93f)
        val indigo = Color(0.29f, 0f, 0.51f)
        val lavender = Color(0.90f, 0.90f, 0.98f)
        val plum = Color(0.87f, 0.63f, 0.87f)
        val orchid = Color(0.85f, 0.44f, 0.84f)

        // Pinks
        val pink = Color(1f, 0.75f, 0.80f)
        val hotPink = Color(1f, 0.41f, 0.71f)
        val deepPink = Color(1f, 0.08f, 0.58f)
        val rose = Color(1f, 0f, 0.50f)

        // Browns
        val brown = Color(0.65f, 0.16f, 0.16f)
        val chocolate = Color(0.82f, 0.41f, 0.12f)
        val sienna = Color(0.63f, 0.32f, 0.18f)
        val tan = Color(0.82f, 0.71f, 0.55f)
        val beige = Color(0.96f, 0.96f, 0.86f)

        // Special / UI
        val coolPurple = Color(0.0667f, 0.0f, 0.0902f)
        val discordBlurple = Color(0.35f, 0.39f, 0.85f)
        val warning = Color(1f, 0.75f, 0f)
        val error = Color(0.90f, 0.20f, 0.20f)
        val success = Color(0.20f, 0.80f, 0.20f)
        val info = Color(0.20f, 0.60f, 1f)
    }
}