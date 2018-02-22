package site.hackery.transit.api

abstract class CancellableEvent {
    @get:JvmName("isCancelled")
    var cancelled = false

    fun cancel() {
        cancelled = true
    }
}
