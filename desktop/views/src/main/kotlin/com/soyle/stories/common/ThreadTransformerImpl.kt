package com.soyle.stories.common

import com.soyle.stories.soylestories.ApplicationScope
import javafx.application.Platform
import kotlinx.coroutines.*
import tornadofx.FX
import tornadofx.runLater

class AsyncThreadTransformer(val applicationScope: ApplicationScope) : ThreadTransformer {

    override val asyncScope: CoroutineScope = applicationScope

    var exceptionHandler: CoroutineExceptionHandler? = null

    override fun async(task: suspend CoroutineScope.() -> Unit): Job {
        val handler = exceptionHandler
        return if (handler == null) {
            applicationScope.launch {
                withTimeout(7000) {
                    task()
                }
            }
        } else {
            applicationScope.launch(handler) {
                withTimeout(7000) {
                    task()
                }
            }
        }
    }

    override fun isGuiThread(): Boolean = Platform.isFxApplicationThread()

    override fun gui(update: suspend CoroutineScope.() -> Unit) {
        FX.runAndWait {
            runBlocking {
                withTimeout(10000) {
                    update()
                }
            }
        }
    }
}