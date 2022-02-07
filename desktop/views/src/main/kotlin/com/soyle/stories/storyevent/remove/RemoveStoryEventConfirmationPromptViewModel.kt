package com.soyle.stories.storyevent.remove

import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.common.Confirmation
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptViewModel
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.CompletableDeferred
import tornadofx.*

class RemoveStoryEventConfirmationPromptViewModel : RemoveStoryEventConfirmationPrompt, ConfirmationPromptViewModel() {

    private val _items = observableListOf<String>()
    val items: List<String> by lazy { FXCollections.unmodifiableObservableList(_items) }
    fun items(): ObservableList<String> = items as ObservableList<String>

    private val _hasMultiple by lazy { booleanBinding(_items) { _items.size > 1 } }
    fun hasMultiple(): ObservableValue<Boolean> = _hasMultiple

    override suspend fun confirmRemoveStoryEventsFromProject(storyEvents: List<StoryEvent>): Confirmation<ConfirmationPrompt.Response> {
        val confirmation = CompletableDeferred<Confirmation<ConfirmationPrompt.Response>>()

        _items.setAll(storyEvents.map { it.name.value })
        _isNeeded.set(true)

        onConfirm = { confirmation.complete(Confirmation(ConfirmationPrompt.Response.Confirm, !doNotShowAgain)) }
        onCheck = { confirmation.complete(Confirmation(ConfirmationPrompt.Response.ShowRamifications, !doNotShowAgain)) }
        onCancel = { confirmation.cancel() }

        return confirmation.await().also { _isNeeded.set(false) }
    }

}