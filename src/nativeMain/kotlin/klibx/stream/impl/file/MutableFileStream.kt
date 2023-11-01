package klibx.stream.impl.file

import klibx.io.File
import klibx.stream.MutableStream
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
internal class MutableFileStream(
    file: File
): ReadableFileStream(file), MutableStream {

    override fun writeByte(byte: Byte) {
        if (closed)
            throw klibx.exception.ClosedException()

        file.appendByte(byte)
    }

    override fun writeBytes(bytes: ByteArray, offset: Int, length: Int) {
        if (closed)
            throw klibx.exception.ClosedException()

        file.appendBytes(bytes, offset, length)
    }

    override fun writeBytes(bytes: ByteArray) {
        if (closed)
            throw klibx.exception.ClosedException()

        file.appendBytes(bytes)
    }

    override fun writeBytes(bytes: CPointer<ByteVar>, offset: Int, length: Int) {
        if (closed)
            throw klibx.exception.ClosedException()

        file.appendBytes(bytes, offset, length)
    }

}