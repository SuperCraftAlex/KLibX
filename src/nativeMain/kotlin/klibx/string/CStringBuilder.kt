package klibx.string

import klibx.exception.ClosedException
import kotlinx.cinterop.*

/**
 * A simple string builder that uses a native heap allocated array of bytes.
 * This is useful for when you are reading bytes from a file and then want to convert that to a string with encoding.
 *
 * @param allocSize The initial size (in bytes / chars) of the StringBuilder's internal array.
 * @property closed Whether this builder has been closed.
 */
@OptIn(ExperimentalStdlibApi::class, ExperimentalForeignApi::class)
class CStringBuilder(
    private var allocSize: Int = 256,
): AutoCloseable {

    var closed: Boolean = false
        private set

    private var alloc = nativeHeap.allocArray<ByteVar>(allocSize)
    private var length = 0

    private fun ensureSpaceForExtra(extra: Int) {
        if (length + extra >= allocSize) {
            allocSize = allocSize * 2 + extra

            val newAlloc = nativeHeap.allocArray<ByteVar>(allocSize)
            for (i in 0..<length) {
                newAlloc[i] = alloc[i]
            }
            nativeHeap.free(alloc)
            alloc = newAlloc
        }
    }

    /**
     * Appends a byte to the end of this string builder.
     * @param char The byte to append.
     */
    fun append(char: Byte) {
        ensureSpaceForExtra(1)
        alloc[length++] = char
    }

    /**
     * Appends a string to the end of this string builder.
     * @param str The string to append.
     */
    fun append(str: String) {
        ensureSpaceForExtra(str.length)
        for (element in str) {
            alloc[length++] = element.code.toByte()
        }
    }

    /**
     * Appends an array of bytes to the end of this string builder.
     * @param bytes The array of bytes to append.
     */
    fun append(bytes: ByteArray) {
        ensureSpaceForExtra(bytes.size)
        for (element in bytes) {
            alloc[length++] = element
        }
    }

    /**
     * Appends an array of bytes to the end of this string builder.
     * @param bytes The array of bytes to append.
     */
    fun append(bytes: CPointer<ByteVar>, length: Int) {
        ensureSpaceForExtra(length)
        for (i in 0 until length) {
            alloc[this.length++] = bytes[i]
        }
    }

    /**
     * Appends a null terminated C string to the end of this string builder.
     */
    fun appendCString(cString: CPointer<ByteVar>) {
        var i = 0
        while (cString[i] != 0.toByte()) {
            alloc[length++] = cString[i++]
        }
    }

    operator fun plusAssign(char: Char) =
        append(char.code.toByte())

    operator fun plusAssign(char: Byte) =
        append(char)

    operator fun plusAssign(str: String) =
        append(str)

    operator fun plusAssign(bytes: ByteArray) =
        append(bytes)

    /**
     * Closes this string builder and frees the native memory.
     * After this is called, this builder is no longer usable.
     */
    override fun close() {
        if (closed) return
        nativeHeap.free(alloc)
        closed = true
    }

    override fun toString(): String {
        if (closed) throw ClosedException()
        alloc[length] = 0
        return alloc.toKString()
    }

}