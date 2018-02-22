package site.hackery.transit.impl.performance

import site.hackery.transit.api.Listener

private class ListenerTrackerData(val wrappingListener: ListenerWrapper<*>,
                                  val rawListener: Listener<*>,
                                  val containingSet: MutableSet<ListenerWrapper<*>>)

class ListenerTracker {
    private val dataSet: MutableMap<Int, ListenerTrackerData> = mutableMapOf()
    private val listenerIDMap: MutableMap<Listener<*>, Int> = mutableMapOf()

    private var counter = 0

    fun add(wrappingListener: ListenerWrapper<*>, containingSet: MutableSet<ListenerWrapper<*>>): Int {
        counter++

        val data = ListenerTrackerData(wrappingListener, wrappingListener.listener, containingSet)
        dataSet[counter] = data

        listenerIDMap[wrappingListener.listener] = counter

        return counter
    }

    fun getContainingSet(id: Int): MutableSet<ListenerWrapper<*>>? {
        return dataSet[id]?.containingSet
    }

    fun getListenerWrapper(id: Int): ListenerWrapper<*>? {
        return dataSet[id]?.wrappingListener
    }

    fun removeReference(id: Int) {
        listenerIDMap.remove(dataSet[id]?.rawListener)
        dataSet.remove(id)
    }

    fun getID(rawListener: Listener<*>): Int? {
        return listenerIDMap[rawListener]
    }
}
