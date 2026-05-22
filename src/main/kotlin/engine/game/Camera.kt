package engine.game

// Asegúrate de que tus métodos de extensión estén importados como funciones de clase
import engine.utils.lookAtLH
import engine.utils.rotationRollPitchYaw
import engine.utils.scalarModAngle
import engine.utils.transformNormal
import glm_.glm.clamp
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import kotlin.math.PI

object Camera {
    const val PITCH_EPSILON = 0.995f

    var position = Vec3()

    var pitch = 0f
        private set

    var yaw = 0f
        private set

    var roll = 0f
        private set

    var moveSpeed = 5f
    var rotationSpeed = 0.012f

    fun getViewMatrix() : Mat4 {
        val cameraRotation = rotationRollPitchYaw(pitch, yaw, roll)

        val forward = Vec3(0f, 0f, 1f)
        val lookVector = forward.transformNormal(cameraRotation)
        val upVector = Vec3(0f, 1f, 0f)

        val cameraTarget = position + lookVector

        return lookAtLH(position, cameraTarget, upVector)
    }

    fun rotate(deltaX: Float, deltaY: Float) {
        yaw = scalarModAngle(yaw + deltaX * rotationSpeed)

        val halfPI = PI.toFloat() / 2f
        pitch = clamp(pitch + deltaY * rotationSpeed, -halfPI * PITCH_EPSILON, halfPI * PITCH_EPSILON)
    }

    fun translate(velocity: Vec3) {
        // CORREGIDO: Solo usamos 'yaw' para que el movimiento sea plano (tipo FPS tradicional).
        // Si quieres que vuele libremente tipo modo espectador (noclip), vuelve a poner 'pitch'.
        val moveRotation = rotationRollPitchYaw(0f, yaw, 0f)
        position.plusAssign(velocity.transformNormal(moveRotation))
    }
}