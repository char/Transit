package site.hackery.transit.impl.performance

import site.hackery.transit.api.Priority
import java.util.concurrent.CopyOnWriteArraySet

class ListenerRegistry {
    val listeners: Array<MutableSet<ListenerWrapper<*>>> = Array(Priority.values().size, { CopyOnWriteArraySet<ListenerWrapper<*>>() })

    operator fun get(priority: Priority): MutableSet<ListenerWrapper<*>> {
        return listeners[priority.ordinal]
    }
}
