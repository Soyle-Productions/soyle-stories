package com.soyle.stories.common

import javafx.application.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.testfx.framework.junit5.ApplicationTest
import kotlin.coroutines.coroutineContext


class SyncThreadTransformer : ThreadTransformer {
    override fun async(task: suspend CoroutineScope.() -> Unit) {
        runBlocking {
            task()
        }
    }

    override fun gui(update: suspend CoroutineScope.() -> Unit) {
        runBlocking {
            update()
        }
    }
}