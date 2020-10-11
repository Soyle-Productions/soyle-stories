package com.soyle.stories.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.testfx.framework.junit5.ApplicationTest

class SyncThreadTransformer : ThreadTransformer, ApplicationTest() {
    override fun async(task: suspend CoroutineScope.() -> Unit) = runBlocking { task() }
    override fun gui(update: suspend CoroutineScope.() -> Unit) {
        interact { runBlocking { update() } }
    }

}