package klibx.stream

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import klibx.stream.exception.EmptyStreamException
import klibx.exception.ClosedException
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * A readable stream.
 * Data will be read from the beginning of the stream.
 * @see MutableStream
 */
@OptIn(ExperimentalStdlibApi::class, ExperimentalForeignApi::class)
interface Stream: AutoCloseable {

    /**
     * Read a byte from the beginning of the stream.
     * @return The byte read.
     * @throws EmptyStreamException
     * @throws ClosedException
     */
    fun readByte(): Byte

    /**
     * Read bytes from the beginning of the stream.
     * @param bytes The array to read into.
     * @param offset The offset to start reading at.
     * @param length The number of bytes to read.
     * @throws EmptyStreamException
     * @throws ClosedException
     */
    fun readBytes(bytes: ByteArray, offset: Int = 0, length: Int)

    /**
     * Read bytes from the beginning of the stream.
     * @param bytes The array to read into.
     * @param offset The offset to start reading at.
     * @param length The number of bytes to read.
     * @throws EmptyStreamException
     * @throws ClosedException
     */
    fun readBytes(bytes: CPointer<ByteVar>, offset: Int = 0, length: Int)

    /**
     * Read bytes from the beginning of the stream.
     * @param length The number of bytes to read. If set to -1, all bytes will be read.
     * @return The bytes read.
     * @throws EmptyStreamException
     * @throws ClosedException
     */
    fun readBytes(length: Int = -1): ByteArray

    val empty: Boolean

    /**
     * Closes the stream.
     */
    override fun close()

}