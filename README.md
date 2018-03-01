# Transit

An event bus for Kotlin.

## Usage:

```kotlin
class MyEvent(val data: Int)

val bus = EventBus()

val registration = bus.register(MyEvent::class, { event ->
    println(event.data)
})

bus.post(MyEvent(1)) // Output: 1
bus.post(MyEvent(2)) // Output: 2

bus.unregister(registration)

bus.post(MyEvent(3)) // No output
```