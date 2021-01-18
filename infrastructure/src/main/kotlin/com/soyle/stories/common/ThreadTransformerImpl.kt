package com.soyle.stories.common

import com.soyle.stories.soylestories.ApplicationScope
import kotlinx.coroutines.*
import tornadofx.FX


class AsyncThreadTransformer(val applicationScope: ApplicationScope) : ThreadTransformer {
    override fun async(task: suspend CoroutineScope.() -> Unit): Job {
        return applicationScope.launch {
            withTimeout(7000) {
                task()
            }
        }
    }

    override fun gui(update: suspend CoroutineScope.() -> Unit) {
        FX.runAndWait {
            runBlocking {
                update()
            }
        }
    }
}