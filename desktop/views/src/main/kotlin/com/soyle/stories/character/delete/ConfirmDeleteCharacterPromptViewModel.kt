package com.soyle.stories.character.delete

import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.common.Confirmation
import com.soyle.stories.domain.character.Character
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptViewModel
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyStringProperty
import kotlinx.coroutines.CompletableDeferred
import tornadofx.booleanProperty
import tornadofx.getValue
import tornadofx.setValue
import tornadofx.stringProperty

class ConfirmDeleteCharacterPromptViewModel : ConfirmationPrompt, ConfirmationPromptViewModel() {

    private val _characterName = stringProperty("")
    fun characterName(): ReadOnlyStringProperty = _characterName
    val characterName: String by _characterName

    override suspend fun confirmDeleteCharacter(character: Character): Confirmation<ConfirmationPrompt.Response>? {
        val deferred = CompletableDeferred<Confirmation<ConfirmationPrompt.Response>?>()

        _characterName.set(character.displayName.value)
        _doNotShowAgain.set(false)
        _isNeeded.set(true)

        onConfirm = { deferred.complete(Confirmation(ConfirmationPrompt.Response.Confirm, !_doNotShowAgain.get())) }
        onCheck = { deferred.complete(Confirmation(ConfirmationPrompt.Response.ShowRamifications, !_doNotShowAgain.get())) }
        onCancel = { deferred.complete(null) }

        return deferred.await().also {
            _isNeeded.set(false)
        }
    }

}