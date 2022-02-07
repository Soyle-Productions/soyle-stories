package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.usecase.character.remove.RemoveCharacterFromStory
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.character.remove.GetPotentialChangesOfRemovingCharacterFromStory
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.getDialogPreference.GetDialogPreferenceController
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferences
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class RemoveCharacterFromStoryControllerImpl(
    private val mainContext: CoroutineContext,
    private val asyncContext: CoroutineContext,
    private val characterRepository: CharacterRepository,
    private val getDialogPreferences: GetDialogPreferences,
    private val setDialogPreferences: SetDialogPreferences,
    private val setDialogPreferencesOutput: SetDialogPreferences.OutputPort,
    private val getPotentialChanges: GetPotentialChangesOfRemovingCharacterFromStory,
    private val removeCharacterFromStory: RemoveCharacterFromStory,
    private val removeCharacterFromStoryOutput: RemoveCharacterFromStory.OutputPort
) : RemoveCharacterFromStoryController {

    private val scope: CoroutineScope = CoroutineScope(asyncContext)

    override fun removeCharacter(characterId: Character.Id, prompt: ConfirmationPrompt, report: RamificationsReport): Job {
        return scope.launch {
            val character = characterRepository.getCharacterOrError(characterId.uuid)
            val preferences = getPreferences()
            if (preferences.shouldShow) {
                val confirmation = withContext(mainContext) { prompt.confirmDeleteCharacter(character) }
                    ?: return@launch
                launch {
                    setDialogPreferences(DialogType.DeleteCharacter, confirmation.showAgain, setDialogPreferencesOutput)
                }
                if (confirmation.choice == ConfirmationPrompt.Response.Confirm) {
                    confirmRemoveCharacter(characterId)
                } else {
                    getPotentialChanges(characterId) {
                        withContext(mainContext) { report.showRamifications(it) }
                            ?: return@getPotentialChanges
                        confirmRemoveCharacter(characterId)
                    }
                }
            } else {
                confirmRemoveCharacter(characterId)
            }
        }
    }

    private suspend fun getPreferences(): DialogPreference {
        val deferred = CompletableDeferred<DialogPreference>()
        getDialogPreferences(DialogType.DeleteCharacter, object : GetDialogPreferences.OutputPort {
            override fun failedToGetDialogPreferences(failure: Exception) {
                deferred.cancel(CancellationException("", cause = failure))
            }
            override fun gotDialogPreferences(response: DialogPreference) {
                deferred.complete(response)
            }
        })
        return deferred.await()
    }

    private suspend fun confirmRemoveCharacter(characterId: Character.Id) {
        removeCharacterFromStory(characterId, removeCharacterFromStoryOutput)
    }

    fun finalize() { scope.cancel() }

}