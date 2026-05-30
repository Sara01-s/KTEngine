package engine.systems

import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import java.nio.ByteBuffer
import java.nio.IntBuffer

object AudioSystem : AutoCloseable {
    private var device: Long = 0
    private var context: Long = 0

    fun init() {
        device = alcOpenDevice(null as ByteBuffer?)

        if (device == 0L) {
            error("Failed to open OpenAL device")
        }

        context = alcCreateContext(device, null as IntBuffer?)

        if (context == 0L) {
            error("Failed to create OpenAL context")
        }

        alcMakeContextCurrent(context)

        AL.createCapabilities(ALC.createCapabilities(device))
    }

    override fun close() {
        alcDestroyContext(context)
        alcCloseDevice(device)
    }
}