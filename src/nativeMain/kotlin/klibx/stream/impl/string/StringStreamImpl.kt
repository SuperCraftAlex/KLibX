package klibx.stream.impl.string

import klibx.stream.Stream
import klibx.stream.StringStream
import klibx.string.CStringBuilder
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
open class StringStreamImpl(
    private val stream: Stream
): StringStream {

    override val empty: Boolean
        get() = stream.empty

    override fun readString(length: Int): String {
        val builder = CStringBuilder(length * 2)
        while (builder.toString().length < length) {
            builder += stream.readByte()
        }
        return builder.toString()
    }

    override fun readString(): String =
        readBytes().decodeToString()

    override fun readLine(): String {
        val builder = CStringBuilder()
        while (true) {
            if (stream.empty)
                break
            val byte = stream.readByte()
            if (byte == '\n'.code.toByte())
                break
            builder += byte
        }
        return builder.toString()
    }

    override fun readByte(): Byte =
        stream.readByte()

    override fun readBytes(bytes: ByteArray, offset: Int, length: Int) =
        stream.readBytes(bytes, offset, length)

    override fun readBytes(bytes: CPointer<ByteVar>, offset: Int, length: Int) =
        stream.readBytes(bytes, offset, length)

    override fun readBytes(length: Int): ByteArray =
        stream.readBytes(length)

    override fun close() =
        stream.close()
}