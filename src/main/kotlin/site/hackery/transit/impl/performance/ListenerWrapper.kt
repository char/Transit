package site.hackery.transit.impl.performance

import site.hackery.transit.api.Listener

data class ListenerWrapper<in T : Any>(val type: Class<*>, val listener: Listener<T>)
