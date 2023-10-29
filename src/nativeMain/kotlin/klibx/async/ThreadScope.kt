package klibx.async

import platform.posix.PTHREAD_SCOPE_PROCESS
import platform.posix.PTHREAD_SCOPE_SYSTEM

class ThreadScope(
    internal val num: Int
) {
    companion object {
        internal val all = mutableMapOf<Int, ThreadScope>()

        val System = ThreadScope(PTHREAD_SCOPE_SYSTEM)
        val Process = ThreadScope(PTHREAD_SCOPE_PROCESS)
    }

    init {
        all[num] = this
    }

}