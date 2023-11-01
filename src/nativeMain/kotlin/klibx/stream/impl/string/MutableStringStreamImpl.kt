package klibx.stream.impl.string

import klibx.stream.MutableStream
import klibx.stream.MutableStringStream
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
open class MutableStringStreamImpl(
    private val stream: MutableStream
): StringStreamImpl(stream), MutableStringStream {
    override fun writeString(string: String) =
        stream.writeBytes(string.encodeToByteArray())

    override fun writeString(string: String, offset: Int, length: Int) {
        val str = string.substring(offset, offset + length)
        stream.writeBytes(str.encodeToByteArray())
    }

    override fun writeLine(string: String) {
        stream.writeBytes(string.encodeToByteArray())
        writeLine()
    }

    override fun writeLine() =
        stream.writeByte('\n'.code.toByte())

    override fun writeByte(byte: Byte) =
        stream.writeByte(byte)

    override fun writeBytes(bytes: ByteArray, offset: Int, length: Int) =
        stream.writeBytes(bytes, offset, length)

    override fun writeBytes(bytes: CPointer<ByteVar>, offset: Int, length: Int) =
        stream.writeBytes(bytes, offset, length)

    override fun writeBytes(bytes: ByteArray) =
        stream.writeBytes(bytes)
}