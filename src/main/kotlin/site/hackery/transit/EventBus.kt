package site.hackery.transit

import site.hackery.transit.api.Listener
import site.hackery.transit.api.Priority
import site.hackery.transit.api.TypedListener
import site.hackery.transit.api.hook.MonitoringHook
import site.hackery.transit.api.hook.StoppingHook
import site.hackery.transit.api.hook.StoppingHookContext
import site.hackery.transit.impl.performance.ListenerRegistry
import site.hackery.transit.impl.performance.ListenerTracker
import site.hackery.transit.impl.performance.ListenerWrapper
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.reflect.KClass

class EventBus {
    private val listeners: ConcurrentMap<Class<*>, ListenerRegistry> = ConcurrentHashMap()
    private val tracker = ListenerTracker()

    private val stoppingHooks: MutableMap<Class<*>, MutableSet<StoppingHook<*>>> = ConcurrentHashMap()
    private val monitoringHooks: MutableMap<Class<*>, MutableSet<MonitoringHook<*>>> = ConcurrentHashMap()

    private fun registerTypeUnsafe(type: Class<*>, listener: Listener<*>, priority: Priority): Int {
        val wrapper = ListenerWrapper(type, listener)
        val set: MutableSet<ListenerWrapper<*>> = listeners.getOrPut(type, { ListenerRegistry() })[priority]
        set += wrapper

        return tracker.add(wrapper, set)
    }

    fun <T : Any> post(event: T): T {
        val type: Class<in Any> = event.javaClass

        val stoppingContext = StoppingHookContext()
        stoppingHooks[type]?.asSequence()
                ?.map {
                    @Suppress("UNCHECKED_CAST")
                    it as StoppingHook<T>
                }?.forEach { it.receive(event, stoppingContext) }

        if (stoppingContext.stopped)
            return event

        listeners[type]?.listeners?.forEach { listenersForPriority ->
            listenersForPriority.asSequence()
                    .map {
                        @Suppress("UNCHECKED_CAST")
                        it.listener as Listener<T>
                    }
                    .forEach { it.fire(event) }
        }

        monitoringHooks[type]?.asSequence()
                ?.map {
                    @Suppress("UNCHECKED_CAST")
                    it as MonitoringHook<T>
                }
                ?.forEach { it.receive(event) }

        return event
    }

    @JvmOverloads
    fun <T : Any> register(type: Class<T>, listener: Listener<T>, priority: Priority = Priority.NORMAL): Int {
        return registerTypeUnsafe(type, listener, priority)
    }

    fun <T : Any> unregister(listener: Listener<T>) {
        val id: Int? = tracker.getID(listener)
        if (id != null) {
            unregister(id)
        }
    }

    fun unregister(id: Int) {
        tracker.getContainingSet(id)?.remove(tracker.getListenerWrapper(id))
        tracker.removeReference(id)
    }

    @JvmOverloads
    fun <T : Any> register(typedListener: TypedListener<T>, priority: Priority = Priority.NORMAL): Int {
        val listenerPriority = if (priority == Priority.NORMAL) typedListener.priority else priority

        return registerTypeUnsafe(typedListener.type, typedListener.listener, listenerPriority)
    }

    fun <T : Any> unregister(typedListener: TypedListener<T>) {
        unregister(typedListener.listener)
    }

    fun <T : Any> register(type: KClass<T>, listener: (T) -> Unit, priority: Priority = Priority.NORMAL): Int {
        return this.register(type.java, object : Listener<T> {
            override fun fire(event: T) {
                listener(event)
            }
        }, priority)
    }

    fun <T : Any> registerHook(type: Class<T>, stoppingHook: StoppingHook<T>) {
        stoppingHooks.computeIfAbsent(type, { mutableSetOf() })
        stoppingHooks[type]!! += stoppingHook
    }

    fun <T : Any> registerHook(type: Class<T>, monitoringHook: MonitoringHook<T>) {
        monitoringHooks.computeIfAbsent(type, { mutableSetOf() })
        monitoringHooks[type]!! += monitoringHook
    }

    fun <T : Any> unregisterHook(type: Class<T>, stoppingHook: StoppingHook<T>) {
        stoppingHooks[type]?.remove(stoppingHook)
    }

    fun <T : Any> unregisterHook(type: Class<T>, monitoringHook: MonitoringHook<T>) {
        monitoringHooks[type]?.remove(monitoringHook)
    }
}
