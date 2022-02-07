package com.soyle.stories.storyevent.coverage.uncover

import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredByScene
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.coverage.uncover.GetPotentialChangesFromUncoveringStoryEvent
import com.soyle.stories.usecase.storyevent.coverage.uncover.PotentialChangesFromUncoveringStoryEvent
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromScene
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferences
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface UncoverStoryEventController {

    fun uncoverStoryEventFromScene(
        sceneId: Scene.Id,
        selectionPrompt: UncoverStoryEventPrompt,
        confirmationPrompt: ConfirmUncoverStoryEventPrompt,
        report: UncoverStoryEventRamificationsReport
    ): Job

    fun uncoverStoryEvent(
        storyEventId: StoryEvent.Id,
        prompt: ConfirmUncoverStoryEventPrompt,
        report: UncoverStoryEventRamificationsReport
    ): Job

    class Implementation(
        private val guiContext: CoroutineContext,
        private val asyncContext: CoroutineContext,

        private val storyEvents: StoryEventRepository,
        private val sceneRepository: SceneRepository,

        private val listStoryEventsCoveredByScene: ListStoryEventsCoveredByScene,
        private val getPotentialChangesFromUncoveringStoryEvent: GetPotentialChangesFromUncoveringStoryEvent,

        private val uncoverStoryEvent: UncoverStoryEventFromScene,
        private val uncoverStoryEventOutput: UncoverStoryEventFromScene.OutputPort,

        private val getDialogPreferences: GetDialogPreferences,
        private val setDialogPreferences: SetDialogPreferences,
        private val setDialogPreferencesOutput: SetDialogPreferences.OutputPort,
    ) : UncoverStoryEventController {

        private val scope = CoroutineScope(guiContext)

        override fun uncoverStoryEventFromScene(
            sceneId: Scene.Id,
            selectionPrompt: UncoverStoryEventPrompt,
            confirmationPrompt: ConfirmUncoverStoryEventPrompt,
            report: UncoverStoryEventRamificationsReport
        ): Job {
            return scope.launch(asyncContext) {
                // validate the scene
                val scene = sceneRepository.getSceneOrError(sceneId.uuid)

                // load story events and display result
                try {
                    listStoryEventsCoveredByScene(scene.id) {
                        val storyEventId = withContext(guiContext) {
                            selectionPrompt.requestStoryEventToUncover(it)
                        }
                        val storyEvent = storyEvents.getStoryEventOrError(storyEventId)
                        uncoverStoryEvent(storyEvent, scene, confirmationPrompt, report)
                    }
                } catch (failure: Throwable) {
                    withContext(guiContext) {
                        selectionPrompt.displayFailureToListStoryEvents(failure)
                    }
                }
            }
        }

        override fun uncoverStoryEvent(
            storyEventId: StoryEvent.Id,
            prompt: ConfirmUncoverStoryEventPrompt,
            report: UncoverStoryEventRamificationsReport
        ): Job {
            return scope.launch(asyncContext) {

                val storyEvent = storyEvents.getStoryEventOrError(storyEventId)
                val scene = withContext(asyncContext) {
                    sceneRepository.getSceneOrError(storyEvent.sceneId!!.uuid)
                }
                uncoverStoryEvent(storyEvent, scene, prompt, report)
            }
        }

        private suspend fun uncoverStoryEvent(
            storyEvent: StoryEvent,
            scene: Scene,
            prompt: ConfirmUncoverStoryEventPrompt,
            report: UncoverStoryEventRamificationsReport
        ) {
            val dialogPreferences = getDialogPreferences()
            if (dialogPreferences.shouldShow) {
                val (confirmation, shouldShow) = withContext(guiContext) {
                    prompt.confirmRemoveStoryEventFromScene(storyEvent, scene)
                }
                coroutineScope {
                    launch { updateDialogPreferences(shouldShow) }
                }
                if (confirmation == ConfirmationPrompt.Response.ShowRamifications) {
                    getPotentialChangesFromUncoveringStoryEvent(storyEvent.id) {
                        withContext(guiContext) {
                            report.showRamifications(it)
                        }
                    }
                }
            }

            uncoverStoryEvent.invoke(storyEvent.id, uncoverStoryEventOutput)
        }

        private suspend fun getDialogPreferences(): DialogPreference {
            val deferred = CompletableDeferred<DialogPreference>()
            val type = DialogType.Other(UncoverStoryEventFromScene::class)
            getDialogPreferences(type, object : GetDialogPreferences.OutputPort {
                override fun failedToGetDialogPreferences(failure: Exception) {
                    deferred.complete(DialogPreference(type, true))
                }

                override fun gotDialogPreferences(response: DialogPreference) {
                    deferred.complete(response)
                }
            })
            return deferred.await()
        }

        private suspend fun updateDialogPreferences(shouldShow: Boolean) {
            setDialogPreferences(
                DialogType.Other(UncoverStoryEventFromScene::class),
                shouldShow,
                setDialogPreferencesOutput
            )
        }

        fun finalize() {
            scope.cancel()
        }
    }

}