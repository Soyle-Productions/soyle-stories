package com.soyle.stories.storyevent.coverage

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInScene
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface CoverStoryEventController {

    fun coverStoryEventInScene(sceneId: Scene.Id, prompt: CoverStoryEventPrompt): Job

    companion object {
        fun Implementation(
            projectId: Project.Id,

            guiContext: CoroutineContext,
            asyncContext: CoroutineContext,

            listStoryEventsInProject: ListAllStoryEvents,
            coverStoryEventInScene: CoverStoryEventInScene,
            coverStoryEventInSceneOutput: CoverStoryEventInScene.OutputPort
        ): CoverStoryEventController =
            object : CoverStoryEventController, CoroutineScope by CoroutineScope(guiContext) {
                override fun coverStoryEventInScene(sceneId: Scene.Id, prompt: CoverStoryEventPrompt): Job {
                    return launch {
                        withContext(asyncContext) {
                            listStoryEventsInProject(projectId) { storyEventItems ->
                                val sortedItems = storyEventItems
                                    .filterNot { it.sceneId == sceneId }
                                    .sortedBy {
                                        when (it.sceneId) {
                                            null -> 0
                                            else -> 1
                                        }
                                    }
                                val storyEventToCover = withContext(guiContext) {
                                    prompt.requestStoryEventToCover(sortedItems)
                                } ?: return@listStoryEventsInProject
                                coverStoryEventInScene(storyEventToCover, sceneId, coverStoryEventInSceneOutput)
                            }
                        }
                    }
                }
            }
    }

}