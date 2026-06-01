package engine.utils

object Layers {
    const val NONE        = 0
    const val DEFAULT     = 1 shl 0 // 0001
    const val SCENE       = 1 shl 1 // 0010
    const val UI          = 1 shl 2 // 0100

    const val EVERYTHING  = -1
}