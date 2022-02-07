package com.soyle.stories.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

interface ThreadTransformer {

    fun isGuiThread(): Boolean

    val asyncContext: CoroutineContext
        get() = Dispatchers.Default

    val asyncScope: CoroutineScope

    val guiContext: CoroutineContext
        get() = Dispatchers.Main

    fun async(task: suspend CoroutineScope.() -> Unit): Job
    fun gui(update: suspend CoroutineScope.() -> Unit)

}