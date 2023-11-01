package klibx.stream

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import klibx.exception.ClosedException
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * A writable stream.
 * Data will be written to the end of the stream.
 * @see Stream
 */
@OptIn(ExperimentalForeignApi::class)
interface MutableStream: Stream {

    /**
     * Write a byte to the end of the stream.
     * @param byte The byte to write.
     * @throws ClosedException
     */
    fun writeByte(byte: Byte)

    /**
     * Write bytes to the end of the stream.
     * @param bytes The array of bytes to write.
     * @param offset The offset to start reading at.
     * @param length The number of bytes to write.
     * @throws ClosedException
     */
    fun writeBytes(bytes: ByteArray, offset: Int = 0, length: Int)

    /**
     * Write bytes to the end of the stream.
     * @param bytes The array of bytes to write.
     * @param offset The offset to start reading at.
     * @param length The number of bytes to write.
     * @throws ClosedException
     */
    fun writeBytes(bytes: CPointer<ByteVar>, offset: Int = 0, length: Int)

    /**
     * Write bytes to the end of the stream.
     */
    fun writeBytes(bytes: ByteArray)

    /**
     * Write bytes to the end of the stream.
     * @param byte The byte to write.
     * @throws ClosedException
     */
    operator fun plusAssign(byte: Byte) =
        writeByte(byte)

    /**
     * Write bytes to the end of the stream.
     * @param bytes The array of bytes to write.
     * @throws ClosedException
     */
    operator fun plusAssign(bytes: ByteArray) =
        writeBytes(bytes)

}