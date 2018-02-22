package site.hackery.transit.api

interface Listener<in T : Any> {
    fun fire(event: T)
}
