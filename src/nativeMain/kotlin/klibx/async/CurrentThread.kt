package klibx.async

import klibx.async.exception.ThreadInvalidException
import kotlinx.cinterop.StableRef
import platform.posix.pthread_exit
import platform.posix.pthread_t

/**
 * A thread that is treated as the current thread
 * If this thread is not the current thread, weird things may happen
 */
class CurrentThread(
    p: pthread_t?,
    pidReal: Int
): Thread(
    p,
    pidReal
) {

    override fun toString(): String =
        "CurrentThread(pid=$pid)"

    /**
     * Exits the current thread
     * @param v exit value
     */
    fun exit(v: Any? = null) {
        if (p == null) {
            throw ThreadInvalidException("Thread does not exist")
        }
        pthread_exit(v?.let { StableRef.create(v).asCPointer() })
        kill()
    }

}