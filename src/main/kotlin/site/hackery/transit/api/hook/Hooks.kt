package site.hackery.transit.api.hook

class StoppingHookContext {
    var stopped = false
        private set

    fun stop() {
        stopped = true
    }
}

open class StoppingHook<in T : Any>(private val receiver: (T, StoppingHookContext) -> Unit) {
    fun receive(event: T, context: StoppingHookContext) {
        receiver(event, context)
    }
}

open class MonitoringHook<in T : Any>(private val receiver: (T) -> Unit) {
    fun receive(event: T) {
        receiver(event)
    }
}
