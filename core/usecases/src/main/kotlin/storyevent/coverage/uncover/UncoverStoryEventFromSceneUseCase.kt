package com.soyle.stories.usecase.storyevent.coverage.uncover

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.domain.scene.WithoutChange
import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene
import com.soyle.stories.domain.storyevent.*
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class UncoverStoryEventFromSceneUseCase(
    private val storyEventRepository: StoryEventRepository,
    private val sceneRepository: SceneRepository
) : UncoverStoryEventFromScene {
    override suspend fun invoke(storyEventId: StoryEvent.Id, output: UncoverStoryEventFromScene.OutputPort) {
        val storyEventUpdate = updateStoryEvent(storyEventId)
        output.storyEventUncoveredFromScene(
            storyEventUpdate.change,
            updateScene(storyEventUpdate.change.previousSceneId, storyEventUpdate.storyEvent)
        )
    }

    private suspend fun updateStoryEvent(storyEventId: StoryEvent.Id): SuccessfulStoryEventUpdate<StoryEventUncoveredFromScene> {
        val update = storyEventRepository.getStoryEventOrError(storyEventId)
            .withoutCoverage()

        when (update) {
            is UnSuccessful -> throw update.reason!!
            is Successful -> {
                storyEventRepository.updateStoryEvent(update.storyEvent)
                return update
            }
        }
    }

    private suspend fun updateScene(sceneId: Scene.Id, storyEvent: StoryEvent): Result<StoryEventRemovedFromScene> {
        val result = kotlin.runCatching { sceneRepository.getSceneOrError(sceneId.uuid) }
        @Suppress("UNCHECKED_CAST")
        val scene = when {
            result.isSuccess -> result.getOrThrow()
            else -> return result as Result<StoryEventRemovedFromScene>
        }
        val sceneUpdate = scene.withoutStoryEvent(storyEvent.id)

        if (sceneUpdate is Updated) {
            sceneRepository.updateScene(sceneUpdate.scene)
        }

        return when (sceneUpdate) {
            is Updated -> Result.success(sceneUpdate.event)
            is WithoutChange -> Result.failure(
                sceneUpdate.reason
                    ?: Exception("Scene ${scene.name.value} does not cover story event ${storyEvent.name.value}")
            )
        }
    }
}