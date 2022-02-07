package com.soyle.stories.storyevent.remove

import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.remove.GetPotentialChangesOfRemovingStoryEventFromProject
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProject
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferences
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface RemoveStoryEventController {

    fun removeStoryEvent(
        storyEventIds: Set<StoryEvent.Id>,
        prompt: RemoveStoryEventConfirmationPrompt,
        report: RemoveStoryEventFromProjectRamificationsReport
    ): Job

    class Implementation(
        private val asyncScope: CoroutineScope,
        private val mainContext: CoroutineContext,

        private val storyEvents: StoryEventRepository,

        private val getRamifications: GetPotentialChangesOfRemovingStoryEventFromProject,

        private val removeStoryEventFromProject: RemoveStoryEventFromProject,
        private val removeStoryEventFromProjectOutput: RemoveStoryEventFromProject.OutputPort,

        private val getDialogPreferences: GetDialogPreferences,
        private val setDialogPreferences: SetDialogPreferences,
        private val setDialogPreferencesOutput: SetDialogPreferences.OutputPort

    ) : RemoveStoryEventController {

        private val asyncContext = asyncScope.coroutineContext
        private val dialogType = DialogType.Other(RemoveStoryEventFromProject::class)

        override fun removeStoryEvent(
            storyEventIds: Set<StoryEvent.Id>,
            prompt: RemoveStoryEventConfirmationPrompt,
            report: RemoveStoryEventFromProjectRamificationsReport
        ): Job {
            return asyncScope.launch {
                val preference = getDialogPreferences()
                if (preference.shouldShow) {
                    val actualStoryEvents = storyEventIds.map { storyEvents.getStoryEventOrError(it) }
                    val (confirmation, shouldShow) = withContext(mainContext) {
                        prompt.confirmRemoveStoryEventsFromProject(actualStoryEvents)
                    }
                    launch { setDialogPreferences(shouldShow) }
                    if (confirmation == ConfirmationPrompt.Response.ShowRamifications) {
                        actualStoryEvents.map {
                            launch {
                                getRamifications(it.id) {
                                    withContext(mainContext) {
                                        report.showPotentialChanges(it)
                                    }
                                }
                            }
                        }.joinAll()
                    }
                }
                storyEventIds.forEach {
                    removeStoryEventFromProject(it, removeStoryEventFromProjectOutput)
                }
            }
        }

        private suspend fun getDialogPreferences(): DialogPreference
        {
            val deferred = CompletableDeferred<DialogPreference>()
            getDialogPreferences(dialogType, object : GetDialogPreferences.OutputPort {
                override fun failedToGetDialogPreferences(failure: Exception) {
                    failure.printStackTrace()
                    deferred.complete(DialogPreference(dialogType, true))
                }
                override fun gotDialogPreferences(response: DialogPreference) {
                    deferred.complete(response)
                }
            })
            return deferred.await()
        }

        private suspend fun setDialogPreferences(shouldShow: Boolean) {
            setDialogPreferences(dialogType, shouldShow, setDialogPreferencesOutput)
        }

    }

}