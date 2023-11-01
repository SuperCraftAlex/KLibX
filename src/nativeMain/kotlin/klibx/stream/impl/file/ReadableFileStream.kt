package klibx.stream.impl.file

import klibx.exception.ClosedException
import klibx.io.File
import klibx.stream.Stream
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
internal open class ReadableFileStream(
    val file: File
): Stream {
    var closed: Boolean = false
        private set

    private var reader = 0

    override fun close() {
        closed = true
        file.close()
    }

    override fun readByte(): Byte =
        if (closed)
            throw ClosedException()
        else
            file.readByteSeq()

    override fun readBytes(bytes: ByteArray, offset: Int, length: Int) {
        if (closed)
            throw ClosedException()

        file.readBytesSeq(bytes.refTo(offset), length)
    }

    override fun readBytes(bytes: CPointer<ByteVar>, offset: Int, length: Int) {
        if (closed)
            throw ClosedException()

        file.readBytesSeq((bytes + offset)!!, length)
    }

    override fun readBytes(length: Int): ByteArray {
        if (closed)
            throw ClosedException()

        val bytes = ByteArray(length)
        file.readBytesSeq(bytes.refTo(0), length)
        return bytes
    }

    override val empty: Boolean
        get() =
            reader >= file.sizeBytes()

}