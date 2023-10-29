package klibx.signal

import kotlinx.cinterop.staticCFunction
import kotlin.system.exitProcess

class Signal(
    val signal: Int,
    val name: String
) {

    companion object {
        private val all = mutableMapOf<Int, Signal>()

        private fun register(signal: Signal) {
            all[signal.signal] = signal
        }

        private fun register(signal: Int, name: String): Signal =
            Signal(signal, name).also {
                register(it)
            }

        operator fun get(signal: Int): Signal? =
            all[signal]

        /**
         * Abort
         */
        val SIGABRT = register(platform.posix.SIGABRT, "SIGABRT")

        /**
         * Floating point exception
         */
        val SIGFPE = register(platform.posix.SIGFPE, "SIGFPE")

        /**
         * Illegal instruction
         */
        val SIGILL = register(platform.posix.SIGILL, "SIGILL")

        /**
         * Interrupt
         */
        val SIGINT = register(platform.posix.SIGINT, "SIGINT")

        /**
         * Segmentation fault
         */
        val SIGSEGV = register(platform.posix.SIGSEGV, "SIGSEGV")

        /**
         * Termination
         */
        val SIGTERM = register(platform.posix.SIGTERM, "SIGTERM")

        val handlerIntDefault: (Int) -> Unit = { sig ->
            val sigi = Signal[sig]
                ?: throw RuntimeException("Unknown signal $sig!")
            sigi.handlerx(sigi)
        }
    }

    private fun raise() {
        platform.posix.raise(signal)
    }

    operator fun invoke() =
        raise()

    override fun toString(): String =
        name

    internal var handlerx: (Signal) -> Unit = {
        throw RuntimeException("Process terminated by signal $it!")
    }

    internal var handlerInt = handlerIntDefault

    init {
        changeHandler(handlerInt)
    }

    private fun changeHandler(handlerIn: (Int) -> Unit) {
        handlerInt = handlerIn
        platform.posix.signal(signal, staticCFunction { sig: Int ->
            Signal[sig]?.let {
                it.handlerInt(sig)
            } ?: throw RuntimeException("Unknown signal $sig!")
            exitProcess(sig)
        } )
    }

    fun handler(handler: (Signal) -> Unit) {
        handlerx = handler
        changeHandler(handlerIntDefault)
    }

    enum class SignalHandler {
        SYSTEM,
        IGNORE,
        ERROR
    }

    fun handler(handler: SignalHandler) {
        when (handler) {
            SignalHandler.SYSTEM -> platform.posix.signal(signal, platform.posix.SIG_DFL)
            SignalHandler.IGNORE -> platform.posix.signal(signal, platform.posix.SIG_IGN)
            SignalHandler.ERROR -> handler {
                throw RuntimeException("Process terminated by signal $it!")
            }
        }
    }

}