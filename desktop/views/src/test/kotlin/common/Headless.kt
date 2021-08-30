package com.soyle.stories.desktop.view.common

import java.lang.Boolean.getBoolean
import kotlin.Any
import kotlin.Exception
import kotlin.IllegalStateException
import kotlin.RuntimeException
import kotlin.String
import kotlin.Throws

fun runHeadless() {
    System.setProperty("testfx.robot", "glass")
    System.setProperty("testfx.headless", "true")
    System.setProperty("prism.order", "sw")
    System.setProperty("prism.text", "t2k")
    System.setProperty("java.awt.headless", "true")
    System.setProperty("headless.geometry", "1600x1200-32")

    try {
        assignMonoclePlatform()
        assignHeadlessPlatform()
    } catch (exception: ClassNotFoundException) {
        throw IllegalStateException(
            "monocle headless platform not found - did you forget to add " +
                    "a dependency on monocle (https://github.com/TestFX/Monocle)?", exception
        )
    } catch (exception: Exception) {
        throw RuntimeException(exception)
    }
}

@Throws(Exception::class)
private fun assignMonoclePlatform() {
    val platformFactoryClass = Class.forName("com.sun.glass.ui.PlatformFactory")
    val platformFactoryImpl = Class.forName("com.sun.glass.ui.monocle.MonoclePlatformFactory")
        .getDeclaredConstructor().newInstance()
    assignPrivateStaticField(platformFactoryClass, "instance", platformFactoryImpl)
}

private fun assignHeadlessPlatform() {
    val nativePlatformFactoryClass = Class.forName("com.sun.glass.ui.monocle.NativePlatformFactory")
    try {
        val nativePlatformCtor = Class.forName(
            "com.sun.glass.ui.monocle.HeadlessPlatform"
        ).getDeclaredConstructor()
        nativePlatformCtor.isAccessible = true
        assignPrivateStaticField(nativePlatformFactoryClass, "platform", nativePlatformCtor.newInstance())
    } catch (exception: ClassNotFoundException) {
        // Before Java 8u40 HeadlessPlatform was located inside of a "headless" package.
        val nativePlatformCtor = Class.forName(
            "com.sun.glass.ui.monocle.headless.HeadlessPlatform"
        ).getDeclaredConstructor()
        nativePlatformCtor.isAccessible = true
        assignPrivateStaticField(nativePlatformFactoryClass, "platform", nativePlatformCtor.newInstance())
    }
}

private fun assignPrivateStaticField(clazz: Class<*>, name: String, value: Any) {
    val field = clazz.getDeclaredField(name)
    field.isAccessible = true
    field[clazz] = value
    field.isAccessible = false
}