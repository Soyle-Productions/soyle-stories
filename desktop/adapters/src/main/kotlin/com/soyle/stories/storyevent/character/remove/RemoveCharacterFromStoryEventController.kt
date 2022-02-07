package com.soyle.stories.storyevent.character.remove

import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.character.remove.GetPotentialChangesOfRemovingCharacterFromStoryEvent
import com.soyle.stories.usecase.storyevent.character.remove.RemoveCharacterFromStoryEvent
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferences
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

interface RemoveCharacterFromStoryEventController {
    fun removeCharacterFromStoryEvent(
        storyEventId: StoryEvent.Id,
        characterId: Character.Id,
        prompt: ConfirmRemoveCharacterFromStoryEventPrompt,
        report: RemoveCharacterFromStoryEventRamificationsReport
    ): Job

    class Implementation(
        private val mainContext: CoroutineContext,
        private val asyncContext: CoroutineContext,

        private val storyEvents: StoryEventRepository,
        private val characters: CharacterRepository,

        private val removeCharacterFromStoryEvent: RemoveCharacterFromStoryEvent,
        private val removeCharacterFromStoryEventOutput: RemoveCharacterFromStoryEvent.OutputPort,

        private val getRamifications: GetPotentialChangesOfRemovingCharacterFromStoryEvent,

        private val getDialogPreferences: GetDialogPreferences,
        private val setDialogPreferences: SetDialogPreferences,
        private val setDialogPreferencesOutput: SetDialogPreferences.OutputPort
    ) : RemoveCharacterFromStoryEventController {

        private val scope = CoroutineScope(asyncContext)

        override fun removeCharacterFromStoryEvent(
            storyEventId: StoryEvent.Id,
            characterId: Character.Id,
            prompt: ConfirmRemoveCharacterFromStoryEventPrompt,
            report: RemoveCharacterFromStoryEventRamificationsReport
        ): Job {
            return scope.launch {

                val dialogPreference = getDialogPreferences()

                if (dialogPreference.shouldShow) {
                    val storyEvent = storyEvents.getStoryEventOrError(storyEventId)
                    val character = characters.getCharacterOrError(characterId.uuid)

                    val (confirmation, showAgain) = withContext(mainContext) {
                        prompt.confirmRemoveCharacter(storyEvent, character)
                    }

                    when(confirmation) {
                        ConfirmationPrompt.Response.Confirm -> confirmed(storyEventId, characterId)
                        ConfirmationPrompt.Response.ShowRamifications -> getRamifications(storyEventId, characterId) {
                            withContext(mainContext) {
                                report.showRamifications(it)
                            }
                            confirmed(storyEventId, characterId)
                        }
                    }

                    updateDialogPreferences(showAgain)
                } else {
                    confirmed(storyEventId, characterId)
                }

            }
        }

        private suspend fun getDialogPreferences(): DialogPreference {
            val deferred = CompletableDeferred<DialogPreference>()
            val type = DialogType.Other(RemoveCharacterFromStoryEvent::class)
            getDialogPreferences(type, object: GetDialogPreferences.OutputPort {
                override fun failedToGetDialogPreferences(failure: Exception) {
                    failure.printStackTrace()
                    deferred.complete(DialogPreference(type, true))
                }

                override fun gotDialogPreferences(response: DialogPreference) {
                    deferred.complete(response)
                }
            })
            return deferred.await()
        }

        private suspend fun updateDialogPreferences(shouldShow: Boolean) {
            val type = DialogType.Other(RemoveCharacterFromStoryEvent::class)
            setDialogPreferences(type, shouldShow, setDialogPreferencesOutput)
        }

        private suspend fun confirmed(storyEventId: StoryEvent.Id, characterId: Character.Id) {
            removeCharacterFromStoryEvent.invoke(
                storyEventId,
                characterId,
                removeCharacterFromStoryEventOutput
            )
        }

        fun finalize() { scope.cancel() }

    }

}