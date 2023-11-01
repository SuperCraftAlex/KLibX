package klibx.stream.impl

import klibx.stream.MutableStream
import klibx.stream.exception.EmptyStreamException
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
class SimpleStream(
    initCapacity: Int = 256,
): MutableStream {

    var closed: Boolean = false
        private set

    var array: ByteArray = ByteArray(initCapacity)
        private set

    var length: Int = 0
        private set

    override val empty: Boolean
        get() =
            (length - readerPos) == 0

    private fun ensureSpaceForExtra(extra: Int) {
        if (length + extra >= array.size) {
            val newCapacity = array.size * 2 + extra

            val newArray = ByteArray(newCapacity)
            array.copyInto(newArray)
            array = newArray
        }
    }

    override fun writeByte(byte: Byte) {
        ensureSpaceForExtra(1)
        array[length++] = byte
    }

    override fun writeBytes(bytes: ByteArray) {
        ensureSpaceForExtra(bytes.size)
        bytes.copyInto(array, length)
        length += bytes.size
    }

    override fun writeBytes(bytes: ByteArray, offset: Int, length: Int) {
        ensureSpaceForExtra(length)
        bytes.copyInto(array, this.length, offset, offset + length)
        this.length += length
    }

    override fun writeBytes(bytes: CPointer<ByteVar>, offset: Int, length: Int) {
        ensureSpaceForExtra(length)
        for (i in offset until length + offset) {
            array[this.length++] = bytes[i]
        }
    }

    private var readerPos: Int = 0

    private fun endReader() {
        if (readerPos == 0) return

        readerPos = 0
        val newArr = ByteArray(array.size - 10)
        array.copyInto(newArr, 0, 10)
        array = newArr
    }

    override fun readByte(): Byte {
        if (readerPos > 9) {
            endReader()
        }
        return array[readerPos++]
    }

    override fun readBytes(length: Int): ByteArray {
        if (this.length - readerPos < length) {
            throw EmptyStreamException()
        }
        val rLength = if (length == -1) this.length - readerPos else length
        val bytes = ByteArray(rLength)
        array.copyInto(bytes, 0, readerPos, readerPos + rLength)
        readerPos += rLength
        if (readerPos > 9) {
            endReader()
        }
        return bytes
    }

    override fun readBytes(bytes: ByteArray, offset: Int, length: Int) {
        if (this.length - readerPos < length) {
            throw EmptyStreamException()
        }
        array.copyInto(bytes, offset, readerPos, readerPos + length)
        readerPos += length
        if (readerPos > 9) {
            endReader()
        }
    }

    override fun readBytes(bytes: CPointer<ByteVar>, offset: Int, length: Int) {
        if (this.length - readerPos < length) {
            throw EmptyStreamException()
        }
        for (i in offset until length + offset) {
            bytes[i] = array[readerPos++]
        }
        if (readerPos > 9) {
            endReader()
        }
    }

    override fun close() {
        closed = true
    }

}