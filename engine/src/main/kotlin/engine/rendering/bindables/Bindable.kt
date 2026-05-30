package engine.rendering.bindables

abstract class Bindable : AutoCloseable {
    companion object {
        const val DEFAULT_GPU_ID = 0
    }

    var gpuID: Int = DEFAULT_GPU_ID

    abstract fun bind()
    abstract fun unbind()
}