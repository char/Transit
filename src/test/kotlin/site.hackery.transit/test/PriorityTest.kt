package site.hackery.transit.test

import org.junit.Assert
import org.junit.Test
import site.hackery.transit.EventBus
import site.hackery.transit.api.Priority

class PriorityTest {
    @Test
    fun test() {
        val bus = EventBus()

        val globalEvent = PriorityTestEvent()
        globalEvent.one = 1
        globalEvent.two = 2

        bus.register(PriorityTestEvent::class, { if (globalEvent.one == 1) globalEvent.one = 0 }, Priority.BEFORE)
        bus.register(PriorityTestEvent::class, { if (globalEvent.one == 0) globalEvent.two = 1 }, Priority.NORMAL)
        bus.register(PriorityTestEvent::class, { if (globalEvent.two == 1) globalEvent.two = 0 }, Priority.AFTER)

        bus.post(globalEvent)

        Assert.assertEquals(0, globalEvent.one)
        Assert.assertEquals(0, globalEvent.two)
    }

    class PriorityTestEvent {
        var one: Int = 0
        var two: Int = 0
    }
}
