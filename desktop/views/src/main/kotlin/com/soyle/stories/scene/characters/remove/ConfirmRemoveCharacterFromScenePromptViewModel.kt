package com.soyle.stories.scene.characters.remove

import com.soyle.stories.ramifications.confirmation.ConfirmationPromptViewModel
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterConfirmationPrompt
import com.soyle.stories.usecase.scene.storyevent.list.StoryEventInSceneItem
import javafx.beans.property.ReadOnlyStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import tornadofx.stringProperty
import tornadofx.getValue
import tornadofx.observableListOf

class ConfirmRemoveCharacterFromScenePromptViewModel : ConfirmationPromptViewModel(),
    RemoveCharacterConfirmationPrompt {

    private val _characterName = stringProperty()
    fun characterName(): ReadOnlyStringProperty = _characterName
    val characterName: String by _characterName

    private val _sceneName = stringProperty()
    fun sceneName(): ReadOnlyStringProperty = _sceneName
    val sceneName: String by _sceneName

    private val _items = observableListOf<StoryEventInSceneItem>()
    val items: ObservableList<StoryEventInSceneItem> by lazy { FXCollections.unmodifiableObservableList(_items) }

    override suspend fun confirmRemoval(
        sceneName: String,
        characterName: String,
        stillInvolvedIn: List<StoryEventInSceneItem>
    ) {
        val deferred = Job()

        _doNotShowAgain.set(false)
        _characterName.set(characterName)
        _sceneName.set(sceneName)
        _items.setAll(stillInvolvedIn)
        _isNeeded.set(true)

        onConfirm = { deferred.complete() }
        onCancel = { deferred.cancel() }

        return deferred.join().also { _isNeeded.set(false) }
    }

}