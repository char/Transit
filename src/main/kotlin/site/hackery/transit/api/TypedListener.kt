package site.hackery.transit.api

import kotlin.reflect.KClass

class TypedListener<T : Any>(val type: Class<T>, val listener: Listener<T>, val priority: Priority = Priority.NORMAL) {
    constructor(type: KClass<T>, listener: (T) -> Unit, priority: Priority = Priority.NORMAL) : this(type.java, object : Listener<T> {
        override fun fire(event: T) {
            listener(event)
        }
    }, priority)
}
