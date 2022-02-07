package com.soyle.stories.usecase.storyevent.coverage.cover

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.SuccessfulStoryEventUpdate
import com.soyle.stories.domain.storyevent.UnSuccessfulStoryEventUpdate
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.Result.Companion.success

class CoverStoryEventInSceneUseCase(
    private val storyEvents: StoryEventRepository,
    private val scenes: SceneRepository
) : CoverStoryEventInScene {

    override suspend fun invoke(
        storyEventId: StoryEvent.Id,
        sceneId: Scene.Id,
        output: CoverStoryEventInScene.OutputPort
    ): Result<Nothing?> {
        val storyEvent = storyEvents.getStoryEventById(storyEventId) ?: return Result.failure(
            StoryEventDoesNotExist(
                storyEventId.uuid
            )
        )
        val scene = scenes.getSceneById(sceneId) ?: return Result.failure(SceneDoesNotExist(sceneId.uuid))

        val update = storyEvent.coveredByScene(scene.id)
        when (update) {
            is UnSuccessfulStoryEventUpdate -> return Result.failure(update.reason!!)
            is SuccessfulStoryEventUpdate -> storyEvents.updateStoryEvent(update.storyEvent)
        }

        output.storyEventCoveredByScene(update.change)

        coroutineScope {
            launch {
               // kotlin.runCatching { addStoryEventToScene.invoke(update.change) }
            }
            update.change.uncovered?.let {
                launch {
                   // kotlin.runCatching { removeStoryEventFromScene(it) }
                }
            }
        }

        return success(null)
    }

}