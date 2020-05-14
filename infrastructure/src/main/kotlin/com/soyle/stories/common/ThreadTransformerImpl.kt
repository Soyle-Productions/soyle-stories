/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 10:44 PM
 */
package com.soyle.stories.common

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.soylestories.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout


class AsyncThreadTransformer(val applicationScope: ApplicationScope) : ThreadTransformer {
    override fun async(task: suspend CoroutineScope.() -> Unit) {
        applicationScope.launch {
            withTimeout(7000) {
                task()
            }
        }
    }

    override fun gui(update: suspend CoroutineScope.() -> Unit) {
        applicationScope.launch(Dispatchers.JavaFx) {
            update()
        }
    }
}