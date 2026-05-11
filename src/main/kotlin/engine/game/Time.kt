package engine.game

class Time {

    companion object {

        const val FIXED_DELTA_TIME = 1f / 60f

        var timeScale = 1f

        private var lastTime = 0.0

        var deltaTime = 0f
            private set

        var time = 0f
            private set

        private var accumulator = 0f

        fun update(currentTime: Double) {
            if (lastTime == 0.0) {
                lastTime = currentTime
            }

            val frameTime = (currentTime - lastTime).toFloat() * timeScale
            lastTime = currentTime

            deltaTime = frameTime
            time += frameTime

            accumulator += frameTime
        }

        fun shouldRunFixedUpdate(): Boolean {
            return accumulator >= FIXED_DELTA_TIME
        }

        fun consumeFixedUpdate() {
            accumulator -= FIXED_DELTA_TIME
        }

        fun reset() {
            lastTime = 0.0
            deltaTime = 0f
            time = 0f
            accumulator = 0f
        }
    }
}