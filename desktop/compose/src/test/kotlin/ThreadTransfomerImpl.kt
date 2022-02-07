package com.soyle.stories.desktop.view

import com.soyle.stories.common.ThreadTransformer
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ThreadTransfomerImpl: ThreadTransformer, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = asyncContext

    override fun async(task: suspend CoroutineScope.() -> Unit): Job = launch { task() }

    override fun gui(update: suspend CoroutineScope.() -> Unit) {
        launch {
            withContext(guiContext) { update() }
        }
    }

    override fun isGuiThread(): Boolean = Thread.currentThread().name == "main"
}