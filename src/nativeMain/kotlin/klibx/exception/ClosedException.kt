package klibx.exception

open class ClosedException(
    txt: String = "This object has been closed and can no longer be used!"
): Exception(txt)