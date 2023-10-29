import klibx.io.File

import kotlin.test.Test

@Test fun testF1() {
    val file = File("settings.gradle.kts")

    file.read()
}

@Test fun testF2() {
    val file = File("test.txt")
    file.create()
    val str = "Hello, World!"
    file.write(str)
    assert(file.read() == str)
    file.close()
}