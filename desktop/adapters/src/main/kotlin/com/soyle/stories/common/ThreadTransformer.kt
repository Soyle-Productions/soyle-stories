package com.soyle.stories.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface ThreadTransformer {

    fun async(task: suspend CoroutineScope.() -> Unit): Job
    fun gui(update: suspend CoroutineScope.() -> Unit)

}