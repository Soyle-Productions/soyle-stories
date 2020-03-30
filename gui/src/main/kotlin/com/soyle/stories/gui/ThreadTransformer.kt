/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 7:05 PM
 */
package com.soyle.stories.gui

import kotlinx.coroutines.CoroutineScope

interface ThreadTransformer {

    fun async(task: suspend CoroutineScope.() -> Unit)
    fun gui(update: suspend CoroutineScope.() -> Unit)

}