package com.soyle.stories.desktop.config.drivers

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.testfx.api.FxRobot

val robot = FxRobot()

fun awaitWithTimeout(timeout: Long, check: () -> Boolean) {
    tailrec suspend fun awaiting() {
        if (check()) return
        delay(10)
        awaiting()
    }
    runBlocking {
        withTimeout(timeout) { awaiting() }
    }
}