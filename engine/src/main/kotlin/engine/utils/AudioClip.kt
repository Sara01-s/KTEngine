package engine.utils

import org.lwjgl.BufferUtils
import org.lwjgl.openal.AL10
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem

class AudioClip(path: String) : AutoCloseable {

    val bufferId: Int

    init {
        val inputStream = javaClass.getResourceAsStream(path)
            ?: error("Audio file not found: $path")

        val audioInput = AudioSystem.getAudioInputStream(BufferedInputStream(inputStream))
        val format = audioInput.format
        val audioBytes = audioInput.readBytes()

        val buffer = BufferUtils.createByteBuffer(audioBytes.size)
        buffer.put(audioBytes)
        buffer.flip()

        val alFormat = when (format.channels) {
            1 -> AL10.AL_FORMAT_MONO16
            2 -> AL10.AL_FORMAT_STEREO16
            else -> error("Unsupported audio format")
        }

        bufferId = AL10.alGenBuffers()

        AL10.alBufferData(bufferId, alFormat, buffer, format.sampleRate.toInt())
    }

    override fun close() {
        AL10.alDeleteBuffers(bufferId)
    }
}