package com.soyle.stories.desktop.config.drivers

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.testfx.api.FxRobot
import tornadofx.UIComponent

val robot = FxRobot()

fun awaitWithTimeout(timeout: Long, check: () -> Boolean) {
    tailrec suspend fun awaiting() {
        if (check()) return
        delay(10)
        awaiting()
    }
    val error = Error("Timed out")
    val timeoutError = runBlocking {
        runCatching {
            withTimeout(timeout) { awaiting() }
        }.exceptionOrNull()
    } ?: return
    error.initCause(timeoutError)
    throw error
}

fun awaitOrContinue(timeout: Long, check: () -> Boolean) {
    tailrec suspend fun awaiting() {
        if (check()) return
        delay(10)
        awaiting()
    }
    runBlocking {
        runCatching {
            withTimeout(timeout) { awaiting() }
        }
    }
}