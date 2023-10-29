package klibx.async

import platform.posix.SCHED_FIFO
import platform.posix.SCHED_OTHER
import platform.posix.SCHED_RR

class ThreadSchedulingPolicy(
    internal val num: Int
) {

    companion object {
        internal val all = mutableMapOf<Int, ThreadSchedulingPolicy>()

        val FirstInFirstOut = ThreadSchedulingPolicy(SCHED_FIFO)
        val RoundRobin = ThreadSchedulingPolicy(SCHED_RR)
        val Other = ThreadSchedulingPolicy(SCHED_OTHER)
    }

    init {
        all[num] = this
    }

}