package klibx.stream

import klibx.stream.impl.string.MutableStringStreamImpl

interface MutableStringStream: StringStream, MutableStream {

    fun writeString(string: String)

    fun writeString(string: String, offset: Int, length: Int)

    fun writeLine(string: String)

    fun writeLine()

    companion object {

        /**
        * Creates a new [MutableStringStream] that wraps the given [MutableStream] instance.
        * It writes the stream as a UTF-8 string.
        */
        fun of(stream: MutableStream): MutableStringStream =
            MutableStringStreamImpl(stream)

    }

}