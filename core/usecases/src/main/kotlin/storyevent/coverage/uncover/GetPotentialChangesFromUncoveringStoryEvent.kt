package com.soyle.stories.usecase.storyevent.coverage.uncover

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.shared.potential.GetPotentialChanges

interface GetPotentialChangesFromUncoveringStoryEvent : GetPotentialChanges<UncoverStoryEventFromScene> {

    suspend operator fun invoke(storyEventId: StoryEvent.Id, output: OutputPort)

    fun interface OutputPort {
        suspend fun receivePotentialChanges(response: PotentialChangesFromUncoveringStoryEvent)
    }

}