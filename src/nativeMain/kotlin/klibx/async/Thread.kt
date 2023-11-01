package klibx.async

import klibx.async.exception.*
import klibx.exception.NotSupportedException
import klibx.signal.Signal
import kotlinx.cinterop.*
import platform.posix.*
import kotlin.native.internal.NativePtr
import klibx.internal.hacks.*
import kotlinx.cinterop.nativeHeap.alloc

@OptIn(ExperimentalForeignApi::class)
open class Thread(
    internal var p: pthread_t? = null,
    private var pidReal: Int? = null
) {

    // TODO: keys

    var name: String
        get() {
            if (p == null) {
                throw ThreadInvalidException("Thread does not exist")
            }
            val buff = ByteArray(1024)
            val res = klibx_pthread_getname_np(p!!, buff.refTo(0), 1024u)
            if (res == FEATURE_NOT_SUPPORTED) {
                throw NotSupportedException("pthread_getname_np")
            }
            if (res != 0) {
                throw ThreadException("Thread name get failed")
            }
            return buff.toKString()
        }
        set(value) {
            if (p == null) {
                throw ThreadInvalidException("Thread does not exist")
            }
            val res = klibx_pthread_setname_np(p!!, value)
            if (res == FEATURE_NOT_SUPPORTED) {
                throw NotSupportedException("pthread_setname_np")
            }
            if (res != 0) {
                throw ThreadException("Thread name set failed")
            }
        }

    val attributes: ThreadAttributes
        get() {
            if (p == null) {
                throw ThreadInvalidException("Thread does not exist")
            }
            val attr = alloc(sizeOf<pthread_attr_t>(), 0).reinterpret<pthread_attr_t>()
            val res = klibx_pthread_getattr_np(p!!, attr.ptr)
            if (res == FEATURE_NOT_SUPPORTED) {
                throw NotSupportedException("pthread_getattr_np")
            }
            if (res != 0) {
                throw ThreadException("Thread attributes get failed")
            }
            return ThreadAttributes(attr, null)
        }

    var detached = false
        private set

    override fun toString(): String {
        if (p == null) {
            return "Thread(invalid)"
        }
        if (pidReal != null) {
            return "Thread(pid=$pidReal,name=$name)"
        }
        return "Thread(name=$name)"
    }

    val pid: Int
        get() = pidReal ?: throw ThreadInvalidException("Thread did not set pid!")

    override fun equals(other: Any?): Boolean {
        if (other !is Thread) {
            return false
        }
        if (p == null && other.p == null) {
            return true
        }
        if (p == null || other.p == null) {
            return false
        }
        return pthread_equal(p!!, other.p!!) != 0
    }

    fun isCurrent(): Boolean =
        equals(current)

    fun signal(signal: Signal) {
        if (p == null) {
            throw ThreadInvalidException("Thread does not exist")
        }
        pthread_kill(p!!, signal.signal)
    }

    companion object {

        private val funs = mutableListOf<StableRef<(Any?) -> Any?>>()
        private val pids = mutableListOf<Int>()

        val current: Thread = CurrentThread(pthread_self(), getpid())

        fun create(
            attr: ThreadAttributes = ThreadAttributes.new(),
            arg: Any? = null,
            code: (Any?) -> Any?,
        ): Thread {
            funs += StableRef.create(code)
            val fr = staticCFunction { ar: COpaquePointer? ->
                pids += getpid()
                val f2 = funs.last().get()
                val arg2 = ar?.asStableRef<Any>()?.get()
                val r = f2(arg2)
                funs.last().dispose()
                funs.dropLast(1)
                r?.let { StableRef.create(r).asCPointer() }
            }
            memScoped {
                val p = alloc<pthread_tVar>()
                val res = pthread_create(
                    p.ptr,
                    attr.attr.ptr,
                    fr,
                    arg?.let { StableRef.create(arg).asCPointer() }
                )
                if (res == -1) {
                    when (posix_errno()) {
                        EAGAIN -> throw ThreadResourceException()
                        EINVAL -> throw ThreadInvalidException("Thread attributes are invalid")
                        EPERM -> throw ThreadPermissionException("Thread creation permission denied")
                        else -> throw ThreadException("Thread creation failed")
                    }
                }
                val pid = pids.lastOrNull()
                if (pids.isNotEmpty()) pids.dropLast(1)
                return Thread(p.value, pid)
            }
        }

    }

    fun cancel() {
        pthread_cancel(p!!)
    }

    fun kill() =
        signal(Signal.SIGTERM)

    fun detach() {
        if (p == null) {
            throw ThreadInvalidException("Thread does not exist")
        }
        val res = pthread_detach(p!!)
        detached = true
        if (res == -1) {
            when (posix_errno()) {
                EINVAL -> throw ThreadInvalidException("Thread can't be detached")
                ESRCH -> throw ThreadInvalidException("Thread does not exist")
                else -> throw ThreadException("Thread detach failed")
            }
        }
    }

    /**
     * @return exit value of the thread
     */
    fun join(): Any? {
        if (p == null) {
            throw ThreadInvalidException("Thread does not exist")
        }
        if (detached) {
            throw ThreadDetachedException()
        }
        memScoped {
            val res2 = alloc<COpaquePointerVar>()
            val res = pthread_join(p!!, res2.ptr)
            p = null
            if (res2.value?.rawValue == NativePtr.NULL) {
                throw ThreadException("Thread join failed")
            }
            if (res2.value == PTHREAD_CANCELED) {
                throw ThreadCancelledException()
            }
            val res2i = res2.value?.asStableRef<Any>()

            if (res == -1) {
                when (posix_errno()) {
                    EDEADLK -> throw ThreadDeadlockException()
                    EINVAL -> throw ThreadInvalidException("Thread can't be joined")
                    ESRCH -> throw ThreadInvalidException("Thread does not exist")
                    else -> throw ThreadException("Thread join failed")
                }
            }

            return res2i?.get()
        }
    }

    override fun hashCode(): Int {
        return p?.hashCode() ?: 0
    }

    // TODO: delay fun

}