package com.soyle.stories.scene.outline.remove

import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.common.Confirmation
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptViewModel
import com.soyle.stories.storyevent.coverage.uncover.ConfirmUncoverStoryEventPrompt
import javafx.beans.property.ReadOnlyStringProperty
import kotlinx.coroutines.CompletableDeferred
import tornadofx.stringProperty
import tornadofx.getValue

class ConfirmRemoveStoryEventFromScenePromptViewModel : ConfirmUncoverStoryEventPrompt, ConfirmationPromptViewModel() {

    var sceneId: Scene.Id? = null
        private set
    var storyEventId: StoryEvent.Id? = null
        private set

    private val _sceneName = stringProperty()
    fun sceneName(): ReadOnlyStringProperty = _sceneName
    val sceneName: String by _sceneName

    private val _storyEventName = stringProperty()
    fun storyEventName(): ReadOnlyStringProperty = _storyEventName
    val storyEventName: String by _storyEventName

    override suspend fun confirmRemoveStoryEventFromScene(
        storyEvent: StoryEvent,
        scene: Scene
    ): Confirmation<ConfirmationPrompt.Response> {
        val deferred = CompletableDeferred<Confirmation<ConfirmationPrompt.Response>>()

        sceneId = scene.id
        _sceneName.set(scene.name.value)

        storyEventId = storyEvent.id
        _storyEventName.set(storyEvent.name.value)

        onConfirm = { deferred.complete(Confirmation(ConfirmationPrompt.Response.Confirm, ! doNotShowAgain)) }
        onCheck = { deferred.complete(Confirmation(ConfirmationPrompt.Response.ShowRamifications, ! doNotShowAgain)) }
        onCancel = { deferred.cancel() }

        _isNeeded.set(true)

        return deferred.await().also {
            _isNeeded.set(false)
        }
    }

}