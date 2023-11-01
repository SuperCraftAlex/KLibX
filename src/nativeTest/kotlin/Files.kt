import klibx.io.File
import kotlin.experimental.ExperimentalNativeApi

import kotlin.test.Test

@Test fun testF1() {
    val file = File("settings.gradle.kts")

    file.read()
}

@OptIn(ExperimentalNativeApi::class)
@Test fun testF2() {
    val file = File("test.txt")
    file.create()
    val str = "Hello, World!"
    file.write(str)
    assert(file.read() == str)
    file.close()
}

@OptIn(ExperimentalNativeApi::class)
@Test fun testF3() {
    val file = File("test.txt")
    file.create()
    file.write("Hello, World!")
    file.delete()
    assert(!file.exists())
}

@OptIn(ExperimentalNativeApi::class)
@Test fun testF4() {
    val file = File("test.txt")
    file.delete()
    file.create()
    val w = file.writer()
    w.writeLine("Hello, ")
    w.writeLine("World!")
    w.close()
    val r = file.reader()
    assert(r.readLine() == "Hello, ")
    assert(r.readLine() == "World!")
}