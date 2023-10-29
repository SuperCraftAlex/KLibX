import klibx.async.Thread
import klibx.async.ThreadAttributes
import klibx.signal.Signal
import kotlin.test.Test

@Test fun testT0() {
    val attr = ThreadAttributes.new()
    Signal.SIGINT.handler(Signal.SignalHandler.IGNORE)
    Signal.SIGTERM.handler(Signal.SignalHandler.IGNORE)
    Signal.SIGILL.handler(Signal.SignalHandler.IGNORE)
    Signal.SIGABRT.handler(Signal.SignalHandler.IGNORE)
    Signal.SIGFPE.handler(Signal.SignalHandler.IGNORE)
    Signal.SIGSEGV.handler(Signal.SignalHandler.IGNORE)
    val t = Thread.create(attr) {
        println("Hello from thread")
    }

    println("Thread created: $t")

    t.join()

    println("Done!")
}