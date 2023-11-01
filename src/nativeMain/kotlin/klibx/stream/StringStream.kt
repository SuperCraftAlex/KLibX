package klibx.stream

import klibx.stream.impl.string.StringStreamImpl

interface StringStream: Stream {

    fun readString(length: Int): String

    fun readString(): String

    fun readLine(): String

    companion object {

        /**
         * Creates a new [StringStream] that wraps the given [Stream] instance.
         * It reads the stream as a UTF-8 string.
         */
        fun of(stream: Stream): StringStream =
            StringStreamImpl(stream)
    }


}