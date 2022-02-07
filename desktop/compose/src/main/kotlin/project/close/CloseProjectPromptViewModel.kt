package com.soyle.stories.desktop.view.project.close

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.soyle.stories.project.closeProject.CloseProjectPrompt
import kotlinx.coroutines.CompletableDeferred

class CloseProjectPromptViewModel : CloseProjectPrompt {

    private val openState = mutableStateOf(false)
    fun openState(): State<Boolean> = openState
    val isOpen: Boolean by openState

    private var deferredConfirmation = CompletableDeferred<Boolean>()

    override suspend fun requestConfirmation(): Boolean {
        openState.value = true
        deferredConfirmation = CompletableDeferred()

        return deferredConfirmation.await()
    }

    fun cancel() {
        deferredConfirmation.complete(false)
        openState.value = false
    }

    fun closeProject() {
        deferredConfirmation.complete(true)
        openState.value = false
    }

}