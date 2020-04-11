/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 10:44 PM
 */
package com.soyle.stories.common

import com.soyle.stories.gui.ThreadTransformer
import javafx.application.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import tornadofx.runAsync
import tornadofx.runLater

object ThreadTransformerImpl : ThreadTransformer {
    override fun async(task: suspend CoroutineScope.() -> Unit) {
        runAsync {
            runBlocking {
                withTimeout(7000) {
                    task()
                }
            }
        }
    }

    override fun gui(update: suspend CoroutineScope.() -> Unit) {
        if (! Platform.isFxApplicationThread()) runLater { gui(update) }
        runBlocking {
            update()
        }
    }
}