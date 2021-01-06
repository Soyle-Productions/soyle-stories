/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 7:05 PM
 */
package com.soyle.stories.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface ThreadTransformer {

    fun async(task: suspend CoroutineScope.() -> Unit): Job
    fun gui(update: suspend CoroutineScope.() -> Unit)

}