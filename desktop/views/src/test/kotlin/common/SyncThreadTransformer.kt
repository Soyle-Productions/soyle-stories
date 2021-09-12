package com.soyle.stories.common

import javafx.application.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.testfx.framework.junit5.ApplicationTest

class SyncThreadTransformer : ThreadTransformer, ApplicationTest() {
    override fun async(task: suspend CoroutineScope.() -> Unit): Job {
        val job = Job()
        runBlocking { task() }
        job.complete()
        return job
    }

    override fun isGuiThread(): Boolean = Platform.isFxApplicationThread()
    override fun gui(update: suspend CoroutineScope.() -> Unit) {
        interact { runBlocking { update() } }
    }

}