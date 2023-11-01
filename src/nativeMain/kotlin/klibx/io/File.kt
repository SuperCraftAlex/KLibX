package klibx.io

import klibx.io.exception.*
import klibx.stream.MutableStringStream
import klibx.stream.StringStream
import klibx.stream.impl.file.MutableFileStream
import klibx.stream.impl.file.ReadableFileStream
import kotlinx.cinterop.*
import platform.posix.*

@OptIn(ExperimentalStdlibApi::class, ExperimentalForeignApi::class)
class File(
    var fp: CPointer<FILE>?,
    val path: String,
    modeIn: Mode? = null,
    val modeChangeable: Boolean = true
): AutoCloseable {

    fun reopen() {
        if (!closed)
            return

        fp = fopen(path, mode?.value ?: throw InvalidModeException())
    }

    fun delete() {
        close()
        if (!exists())
            return
        if (remove(path) != 0)
            throw FileDeleteException()
    }

    fun reader(): StringStream =
        StringStream.of(ReadableFileStream(this.clone()))

    fun writer(): MutableStringStream =
        MutableStringStream.of(MutableFileStream(this.clone().also {
            it.mode = Mode.READ_WRITE
            it.rewind()
        }))

    fun appender(): MutableStringStream =
        MutableStringStream.of(MutableFileStream(this.clone().also {
            it.mode = Mode.APPEND_READ
        }))

    internal fun clone(): File =
        File(fp, path, mode, modeChangeable)

    enum class Mode(
        val value: String,
        val reading: Boolean,
        val writing: Boolean,
        val appending: Boolean,
        val creating: Boolean,
        val createNew: Boolean = false,
        val binary: Boolean = false
    ) {
        READ("r", true, false, false, false),
        WRITE("w", false, true, false, true),
        APPEND("a", false, false, true, true),
        READ_WRITE("r+", true, true, false, false),
        WRITE_READ_CREATING("w+", true, true, false, true, true),
        APPEND_READ("a+", true, false, true, false),
        READ_BINARY("rb", true, false, false, false, false, true),
        WRITE_BINARY("wb", false, true, false, true, false, true),
        APPEND_BINARY("ab", false, false, true, true, false, true),
        READ_WRITE_BINARY("rb+", true, true, false, false, false, true),
        WRITE_READ_BINARY_CREATING("wb+", true, true, false, true, true, true),
        APPEND_READ_BINARY("ab+", true, false, true, true, false, true)
    }

    var closed: Boolean = false
        private set

    var mode: Mode? = modeIn
        set(value) {
            if (field == value)
                return

            if (!modeChangeable)
                throw FileModeNotChangeableException()

            field = value

            if (value == null) {
                fclose(fp)
                fp = null
                return
            }

            if (fp == null) {
                fp = fopen(path, value.value)
                    ?: throw FileOpeningException()
                return
            }

            fp = freopen(path, value.value, fp)
                ?: throw FileOpeningException()
        }

    constructor(path: String, mode: Mode?):
            this(mode?.let { fopen(path, mode.value) }, path, mode)

    constructor(path: String, mode: String):
            this(path, Mode.values().find { it.value == mode }
                ?: throw InvalidModeException())

    constructor(path: String):
            this(path, null)

    override fun close() {
        fp?.let { fclose(fp) }
        fp = null
        closed = true
    }

    override fun toString(): String {
        if (closed || fp == null)
            return "File(closed)"

        return "File(path=\"$path\")"
    }

    private fun readingMode() {
        if (closed)
            throw FileClosedException()

        if (mode?.reading == true)
            return

        mode = Mode.READ
    }

    private fun writingMode() {
        if (closed)
            throw FileClosedException()

        if (mode?.writing == true)
            return

        mode = Mode.WRITE
    }

    private fun writingCreatingMode() {
        if (closed)
            throw FileClosedException()

        if (mode?.creating == true && mode?.writing == true)
            return

        mode = Mode.WRITE
    }

    private fun appendingMode() {
        if (closed)
            throw FileClosedException()

        if (mode?.appending == true)
            return

        mode = Mode.APPEND
    }

    fun sizeBytes(): Long {
        readingMode()

        fseek(fp, 0, SEEK_END)
        val size = ftell(fp)
        rewind(fp)
        return size
    }

    internal fun readByteSeq(): Byte {
        readingMode()

        val byte = fgetc(fp)
        if (byte == EOF) {
            throw EndOfFileException()
        }
        return byte.toByte()
    }

    internal fun readBytesSeq(target: CValuesRef<ByteVar>, size: Int) {
        readingMode()

        if (fread(target, size.toULong(), 1u, fp) != size.toULong()) {
            throw EndOfFileException()
        }
    }

    internal fun rewind() {
        rewind(fp)
    }

    fun readBytes(target: CValuesRef<ByteVar>, size: Int) {
        readingMode()

        rewind()

        readBytesSeq(target, size)
    }

    fun readBytes(): ByteArray {
        val size = sizeBytes().toInt()
        val buffer = ByteArray(size)
        readBytes(buffer.refTo(0), size)
        return buffer
    }

    fun read(): String {
        val size = sizeBytes().toInt()
        val buffer = ByteArray(size)
        readBytes(buffer.refTo(0), size)
        return buffer
            .decodeToString()
    }

    fun writeBytes(bytes: ByteArray) {
        writingMode()

        rewind()
        fwrite(bytes.refTo(0), bytes.size.toULong(), 1u, fp)
    }

    fun write(str: String) =
        writeBytes(str.encodeToByteArray())

    fun create() {
        reopen()
        writingCreatingMode()
    }

    fun appendBytes(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size) {
        appendingMode()

        fwrite(bytes.refTo(offset), length.toULong(), 1u, fp)
    }

    fun appendBytes(bytes: CPointer<ByteVar>, offset: Int = 0, length: Int) {
        appendingMode()

        fwrite(bytes + offset, length.toULong(), 1u, fp)
    }

    fun appendByte(byte: Byte) {
        appendingMode()

        fputc(byte.toInt(), fp)
    }

    fun append(str: String) =
        appendBytes(str.encodeToByteArray())

    fun exists(): Boolean =
        access(path, F_OK) == 0

    fun isWritable(): Boolean =
        access(path, W_OK) == 0

    fun isReadable(): Boolean =
        access(path, R_OK) == 0

    fun isExecutable(): Boolean =
        access(path, X_OK) == 0

}