package site.hackery.transit.test;

import org.junit.Assert;
import org.junit.Test;
import site.hackery.transit.EventBus;

public class JavaTest {
    private String output;

    @Test
    public void test() {
        EventBus bus = new EventBus();

        bus.register(DummyEvent.class, (event) -> {
            output = event.getMessage();
        });

        bus.post(new DummyEvent("Hello!"));
        Assert.assertEquals("Hello!", output);
    }
}
