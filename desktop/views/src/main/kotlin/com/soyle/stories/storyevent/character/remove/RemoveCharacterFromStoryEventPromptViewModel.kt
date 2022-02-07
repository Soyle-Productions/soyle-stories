package com.soyle.stories.storyevent.character.remove

import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.common.Confirmation
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptViewModel
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyStringProperty
import kotlinx.coroutines.CompletableDeferred
import tornadofx.booleanProperty
import tornadofx.getValue
import tornadofx.setValue
import tornadofx.stringProperty

class RemoveCharacterFromStoryEventPromptViewModel : ConfirmRemoveCharacterFromStoryEventPrompt, ConfirmationPromptViewModel() {

    var characterId: Character.Id? = null
        private set

    var storyEventId: StoryEvent.Id? = null
        private set

    private val _characterName = stringProperty("")
    fun characterName(): ReadOnlyStringProperty = _characterName
    val characterName: String by _characterName

    private val _storyEventName = stringProperty("")
    fun storyEventName(): ReadOnlyStringProperty = _storyEventName
    val storyEventName: String by _storyEventName

    override suspend fun confirmRemoveCharacter(
        storyEvent: StoryEvent,
        character: Character
    ): Confirmation<ConfirmationPrompt.Response> {
        val deferred = CompletableDeferred<Confirmation<ConfirmationPrompt.Response>>()

        characterId = character.id
        _characterName.set(character.displayName.value)
        storyEventId = storyEvent.id
        _storyEventName.set(storyEvent.name.value)
        _doNotShowAgain.set(false)
        _isNeeded.set(true)

        onConfirm = { deferred.complete(Confirmation(ConfirmationPrompt.Response.Confirm, !_doNotShowAgain.get())) }
        onCheck = { deferred.complete(Confirmation(ConfirmationPrompt.Response.ShowRamifications, !_doNotShowAgain.get())) }
        onCancel = { deferred.cancel() }

        return deferred.await().also {
            characterId = null
            storyEventId = null
            _isNeeded.set(false)
        }
    }

}