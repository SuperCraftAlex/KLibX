import klibx.signal.Signal
import kotlin.system.exitProcess
import kotlin.test.Test

// commented out because the test can't be run automatically
/* @Test */ fun testS1() {
    val sigint = Signal.SIGINT
    sigint.handler {
        println("signal handler for signal $it")
        exitProcess(0)
    }
    sigint()
    assert(false) // this should not be reached
}

@Test fun testS2() {
    val sigint = Signal.SIGINT
    sigint.handler(Signal.SignalHandler.IGNORE)
    sigint()
    assert(true) // this should be reached
    sigint.handler(Signal.SignalHandler.SYSTEM)
}

@Test fun testS3() {
    val sigint = Signal.SIGINT
    sigint.handler(Signal.SignalHandler.ERROR)
    var caught = false
    try {
        sigint()
    } catch (e: RuntimeException) {
        caught = true
        println("caught exception: $e")
    }
    assert(caught) // this should be reached
    sigint.handler(Signal.SignalHandler.SYSTEM)
}