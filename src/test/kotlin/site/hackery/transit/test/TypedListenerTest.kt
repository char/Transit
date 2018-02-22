package site.hackery.transit.test

import org.junit.Assert
import org.junit.Test
import site.hackery.transit.EventBus
import site.hackery.transit.api.TypedListener

class TypedListenerTest {
    var resultingMessage: String = ""

    @Test
    fun test() {
        Assert.assertEquals("Sanity check, initial value is blank.", "", resultingMessage)

        val bus = EventBus()
        val typedListener: TypedListener<*> = TypedListener(DummyEvent::class, { resultingMessage = it.message })
        bus.register(typedListener)

        bus.post(DummyEvent("Hello, world!"))

        Assert.assertEquals("Hello, world!", resultingMessage)
    }
}
