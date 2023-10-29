package klibx.async.exception

open class ThreadException(message: String):
    Exception(message)

class ThreadCancelledException:
    ThreadException("Thread was already cancelled")

class ThreadDeadlockException:
    ThreadException("Thread deadlock detected")

class ThreadInvalidException(reason: String):
    ThreadException("Thread is invalid: $reason")

class ThreadDetachedException:
    ThreadException("Thread is detached")

class ThreadAttributesImmutableException:
    ThreadException("Thread attributes cannot be changed")

class ThreadResourceException:
    ThreadException("Thread resource limit exceeded")

class ThreadPermissionException(reason: String):
    ThreadException("Thread permission denied: $reason")