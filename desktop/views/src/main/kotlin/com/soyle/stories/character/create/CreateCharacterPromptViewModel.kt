package com.soyle.stories.character.create

import com.soyle.stories.character.buildNewCharacter.CreateCharacterPrompt
import com.soyle.stories.domain.validation.NonBlankString
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.CompletableDeferred
import tornadofx.booleanProperty
import tornadofx.getValue
import tornadofx.runLater
import tornadofx.stringProperty

class CreateCharacterPromptViewModel : CreateCharacterPrompt {

    private val _isOpen = booleanProperty(false)
    fun isOpen(): ObservableValue<Boolean> = _isOpen
    val isOpen: Boolean by _isOpen

    private val _name = stringProperty("")
    fun name(): ObservableValue<String> = _name
    val name: String by _name

    private var onCreate: (NonBlankString) -> Unit = {}
    fun create(name: NonBlankString) {
        onCreate(name)
    }

    private var onCancel: () -> Unit = {}
    fun cancel() {
        onCancel()
    }

    fun close() {
        onCancel()
        _isOpen.set(false)
    }

    override suspend fun requestName(previousAttempt: String?): NonBlankString {
        val deferred = CompletableDeferred<NonBlankString>()

        onCreate = { deferred.complete(it) }
        onCancel = { deferred.cancel() }

        _name.set(previousAttempt.orEmpty())
        _isOpen.set(true)

        return deferred.await().also {
            _isOpen.set(false)
        }
    }
}