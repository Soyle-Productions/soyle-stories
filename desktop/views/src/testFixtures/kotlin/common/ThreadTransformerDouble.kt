package com.soyle.stories.desktop.view.common

import com.soyle.stories.common.ThreadTransformer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking

class ThreadTransformerDouble(
    private val guiUpdate: (update: suspend CoroutineScope.() -> Unit) -> Unit = { update -> runBlocking { update() } }
) : ThreadTransformer {
    var isGuiThreadProp = false

    override val asyncScope: CoroutineScope
        get() = TODO("Not yet implemented")

    override fun isGuiThread(): Boolean = isGuiThreadProp

    override fun gui(update: suspend CoroutineScope.() -> Unit) {
        isGuiThreadProp = true
        guiUpdate(update)
        isGuiThreadProp = false
    }

    override fun async(task: suspend CoroutineScope.() -> Unit): Job = Job()
}