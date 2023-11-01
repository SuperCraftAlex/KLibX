package klibx.async

import klibx.async.exception.ThreadAttributesImmutableException
import klibx.internal.hacks.klibx_pthread_get_sched_priority
import klibx.internal.hacks.klibx_pthread_set_sched_priority
import kotlinx.cinterop.*
import kotlinx.cinterop.nativeHeap.alloc
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
class ThreadAttributes internal constructor(
    internal var attr: pthread_attr_t,
    internal var updateCallback: ((pthread_attr_t) -> Unit)?
) {

    companion object {
        fun new(): ThreadAttributes {
            val attr = alloc(sizeOf<pthread_attr_t>(), 0).reinterpret<pthread_attr_t>()
            if (pthread_attr_init(attr.ptr) != 0) {
                if (posix_errno() == ENOMEM) {
                    throw OutOfMemoryError()
                } else {
                    throw RuntimeException("ThreadAttributes: pthread_attr_init failed")
                }
            }
            return ThreadAttributes(attr, null)
        }
    }

    var policy: ThreadSchedulingPolicy
        get() {
            memScoped {
                val policy = alloc<IntVar>()
                if (pthread_attr_getschedpolicy(attr.ptr, policy.ptr) != 0) {
                    throw RuntimeException("ThreadAttributes: pthread_attr_getschedpolicy failed")
                }
                return ThreadSchedulingPolicy.all[policy.value]!!
            }
        }
        set(v) {
            if (pthread_attr_setschedpolicy(attr.ptr, v.num) != 0) {
                throw RuntimeException("ThreadAttributes: pthread_attr_setschedpolicy failed")
            }
            updateCallback?.invoke(attr)
                ?: throw ThreadAttributesImmutableException()
        }

    var priority: Int
        get() =
            klibx_pthread_get_sched_priority(attr.ptr)
        set(v) {
            klibx_pthread_set_sched_priority(attr.ptr, v)
            updateCallback?.invoke(attr)
                ?: throw ThreadAttributesImmutableException()
        }

    /**
     * Sets the process as detached
     * Does NOT detach a running thread!
     */
    var detached: Boolean
        get() {
            memScoped {
                val state = alloc<IntVar>()
                if (pthread_attr_getdetachstate(attr.ptr, state.ptr) != 0) {
                    throw RuntimeException("ThreadAttributes: pthread_attr_getdetachstate failed")
                }
                return state.value == PTHREAD_CREATE_DETACHED
            }
        }
        set(v) {
            if (pthread_attr_setdetachstate(attr.ptr, if (v) PTHREAD_CREATE_DETACHED else PTHREAD_CREATE_JOINABLE) != 0) {
                throw RuntimeException("ThreadAttributes: pthread_attr_setdetachstate failed")
            }
            updateCallback?.invoke(attr)
                ?: throw ThreadAttributesImmutableException()
        }

    var scope: ThreadScope
        get() {
            memScoped {
                val scope = alloc<IntVar>()
                if (pthread_attr_getscope(attr.ptr, scope.ptr) != 0) {
                    throw RuntimeException("ThreadAttributes: pthread_attr_getscope failed")
                }
                return ThreadScope.all[scope.value]!!
            }
        }
        set(v) {
            if (pthread_attr_setscope(attr.ptr, v.num) != 0) {
                throw RuntimeException("ThreadAttributes: pthread_attr_setscope failed")
            }
            updateCallback?.invoke(attr)
                ?: throw ThreadAttributesImmutableException()
        }

    // TODO: stack guard size, inheriting scheduling policy, custom stack (size and address)

}