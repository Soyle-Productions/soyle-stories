package com.soyle.stories.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

class ViewModelScope(context: CoroutineContext) : CoroutineScope by CoroutineScope(context) {
    fun finalize() { cancel() }
}