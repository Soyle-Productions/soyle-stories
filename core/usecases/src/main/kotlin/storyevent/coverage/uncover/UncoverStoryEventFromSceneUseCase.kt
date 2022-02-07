package com.soyle.stories.usecase.storyevent.coverage.uncover

import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.domain.storyevent.UnSuccessful
import com.soyle.stories.domain.storyevent.*
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class UncoverStoryEventFromSceneUseCase(
    private val storyEventRepository: StoryEventRepository
) : UncoverStoryEventFromScene {
    override suspend fun invoke(storyEventId: StoryEvent.Id, output: UncoverStoryEventFromScene.OutputPort) {
        val storyEventUpdate = updateStoryEvent(storyEventId)
        output.storyEventUncoveredFromScene(
            storyEventUpdate.change
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

}