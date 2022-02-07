package com.soyle.stories.storyevent.coverage.cover

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.scene.list.ListAllScenes
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInScene
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromScene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface StoryEventCoverageController {

    fun modifyStoryEventCoverage(storyEventId: StoryEvent.Id, prompt: StoryEventCoveragePrompt): Job

    class Implementation(
        private val guiContext: CoroutineContext,
        private val asyncContext: CoroutineContext,

        private val storyEventRepository: StoryEventRepository,
        private val listAllScenes: ListAllScenes,
        private val uncoverStoryEvent: UncoverStoryEventFromScene,
        private val uncoverStoryEventOutput: UncoverStoryEventFromScene.OutputPort,
        private val coverStoryEventInScene: CoverStoryEventInScene,
        private val coverStoryEventOutput: CoverStoryEventInScene.OutputPort
    ) : StoryEventCoverageController, CoroutineScope by CoroutineScope(guiContext + Job()){

        override fun modifyStoryEventCoverage(storyEventId: StoryEvent.Id, prompt: StoryEventCoveragePrompt): Job {
            return launch {
                val storyEvent = withContext(asyncContext) {
                    storyEventRepository.getStoryEventOrError(storyEventId)
                }

                withContext(asyncContext) {
                    listAllScenes(storyEvent.projectId) {
                        val sceneSelection = withContext(guiContext) {
                            prompt.requestSceneSelection(storyEvent.sceneId, it)
                        } ?: return@listAllScenes
                        if (sceneSelection == storyEvent.sceneId) {
                            uncoverStoryEvent(storyEvent.id, uncoverStoryEventOutput)
                        } else {
                            coverStoryEventInScene(storyEvent.id, sceneSelection, coverStoryEventOutput)
                        }
                    }
                }
            }
        }
    }

}