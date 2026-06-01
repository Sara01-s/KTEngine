package engine.utils

abstract class ReactiveCommandBase<T> : AutoCloseable {
    private val callbacks = mutableSetOf<CallbackEntry<T>>()

    data class CallbackEntry<T>(
        val action: (T) -> Unit,
        val owner: Any? = null
    )

    val subscribersNames: String
        get() = callbacks.joinToString(",") { it.action::class.simpleName!! }

    fun subscribe(action: (T) -> Unit, owner: Any? = null) {
        callbacks.add(CallbackEntry(action, owner))
    }

    fun unsubscribe(action: (T) -> Unit) {
        callbacks.removeIf { it.action == action }
    }

    fun unsubscribeAllFrom(instance: Any) {
        callbacks.removeIf { it.owner == instance }
    }

    protected fun executeAll(value: T) {
        callbacks.forEach { it.action(value) }
    }

    override fun close() {
        callbacks.clear()
    }
}

class ReactiveUnitCommand : ReactiveCommandBase<Unit>() {
    fun execute() = executeAll(Unit)
}

class ReactiveCommand<T> : ReactiveCommandBase<T>() {
    fun execute(value: T) = executeAll(value)
}

class ReactiveCommand2<T1, T2> : AutoCloseable {
    private val callbacks = mutableSetOf<(T1, T2) -> Unit>()

    fun subscribe(action: (T1, T2) -> Unit) {
        callbacks.add(action)
    }

    fun unsubscribe(action: (T1, T2) -> Unit) {
        callbacks.remove(action)
    }

    fun execute(v1: T1, v2: T2) {
        callbacks.forEach { it(v1, v2) }
    }

    override fun close() {
        callbacks.clear()
    }
}