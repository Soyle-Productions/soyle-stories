package com.soyle.stories.storyevent.coverage

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredByScene
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromScene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface UncoverStoryEventController {

    fun uncoverStoryEventFromScene(sceneId: Scene.Id): Job
    fun uncoverStoryEvent(storyEventId: StoryEvent.Id): Job

    class Implementation(
        private val guiContext: CoroutineContext,
        private val asyncContext: CoroutineContext,

        private val sceneRepository: SceneRepository,
        private val listStoryEventsCoveredByScene: ListStoryEventsCoveredByScene,
        private val uncoverStoryEvent: UncoverStoryEventFromScene,
        private val uncoverStoryEventOutput: UncoverStoryEventFromScene.OutputPort,

        private val getUncoverStoryEventPrompt: (Scene.Id, String) -> UncoverStoryEventPrompt
    ) : UncoverStoryEventController, CoroutineScope by CoroutineScope(guiContext) {
        override fun uncoverStoryEventFromScene(sceneId: Scene.Id): Job {
            return launch {
                // validate the scene
                val scene = withContext(asyncContext) {
                    sceneRepository.getSceneOrError(sceneId.uuid)
                }

                // Inform User Outline Is Being Loaded
                val prompt = getUncoverStoryEventPrompt(scene.id, scene.name.value)

                // load story events and display result
                try {
                    withContext(asyncContext) {
                        listStoryEventsCoveredByScene(scene.id) {
                            val storyEventId = withContext(guiContext) {
                                prompt.requestStoryEventToUncover(it)
                            } ?: return@listStoryEventsCoveredByScene
                            uncoverStoryEvent(storyEventId)
                        }
                    }
                } catch (failure: Throwable) {
                    withContext(guiContext) {
                        prompt.displayFailureToListStoryEvents(failure)
                    }
                }
            }
        }

        override fun uncoverStoryEvent(storyEventId: StoryEvent.Id): Job {
            return launch(asyncContext) {
                uncoverStoryEvent.invoke(storyEventId, uncoverStoryEventOutput)
            }
        }
    }

}