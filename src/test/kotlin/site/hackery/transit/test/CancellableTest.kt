package site.hackery.transit.test

import org.junit.Assert
import org.junit.Test
import site.hackery.transit.EventBus
import site.hackery.transit.api.CancellableEvent


class CancellableTest {
    @Test
    fun test() {
        val bus = EventBus()
        val cancellorId = bus.register(CancellableTestEvent::class, { it.cancel() })

        Assert.assertEquals(true, bus.post(CancellableTestEvent()).cancelled)
        bus.unregister(cancellorId)
        Assert.assertEquals(false, bus.post(CancellableTestEvent()).cancelled)
    }

    class CancellableTestEvent : CancellableEvent()
}
