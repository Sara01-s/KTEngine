package engine.components

import engine.utils.AudioClip
import org.lwjgl.openal.AL10.*

class AudioSource(
    clip: AudioClip? = null
) : Component() {

    private val sourceId = alGenSources()

    var clip: AudioClip? = clip
        set(value) {
            field = value

            if (value != null) {
                alSourcei(sourceId, AL_BUFFER, value.bufferId)
            }
        }

    var volume: Float = 1f
        set(value) {
            field = value
            alSourcef(sourceId, AL_GAIN, value)
        }

    var pitch: Float = 1f
        set(value) {
            field = value
            alSourcef(sourceId, AL_PITCH, value)
        }

    var loop: Boolean = false
        set(value) {
            field = value
            alSourcei(
                sourceId,
                AL_LOOPING,
                if (value) AL_TRUE else AL_FALSE
            )
        }

    fun play() {
        alSourcePlay(sourceId)
    }

    fun pause() {
        alSourcePause(sourceId)
    }

    fun stop() {
        alSourceStop(sourceId)
    }

    override fun close() {
        stop()
        alDeleteSources(sourceId)
    }
}