package com.soyle.stories.desktop.view.project.start

import androidx.compose.runtime.*
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.startNewProject.StartProjectPrompt
import kotlinx.coroutines.CompletableDeferred

class StartProjectPromptViewModel : StartProjectPrompt {

    private val _open = mutableStateOf(false)
    fun isOpen(): State<Boolean> = _open
    var isOpen by _open
        private set

    private val _directory = mutableStateOf<String>("")
    fun directory(): State<String> = _directory
    var directory: String by _directory

    private val _directoryError = mutableStateOf<String?>(null)
    fun directoryError(): State<String?> = _directoryError
    val directoryError by _directoryError

    private var deferredDirectory = CompletableDeferred<String?>()

    private val _name = mutableStateOf<String>("")
    fun name(): State<String> = _name
    var name: String by _name

    private val _nameError = mutableStateOf<String?>(null)
    fun nameError(): State<String?> = _nameError
    val nameError by _nameError

    private var deferredName = CompletableDeferred<NonBlankString?>()

    fun cancel() {
        isOpen = false
        deferredDirectory.cancel()
        deferredName.cancel()
    }

    fun complete() {
        deferredDirectory.complete(directory)
        val nonBlankName = NonBlankString.create(name)
        when (nonBlankName) {
            null -> {}
            else -> {
                deferredName.complete(nonBlankName)
            }
        }
    }

    override fun close() {
        isOpen = false
        deferredDirectory.cancel()
        deferredName.cancel()
    }

    override suspend fun requestDirectory(previousAttempt: String?, errorMessage: String?): String? {
        isOpen = true
        directory = previousAttempt ?: ""
        _directoryError.value = errorMessage ?: ""
        deferredDirectory = CompletableDeferred()

        return deferredDirectory.await()
    }

    override suspend fun requestProjectName(previousAttempt: String?, errorMessage: String?): NonBlankString? {
        isOpen = true
        name = previousAttempt ?: "Untitled"
        _nameError.value = errorMessage ?: ""
        deferredName = CompletableDeferred()

        return deferredName.await()
    }

}