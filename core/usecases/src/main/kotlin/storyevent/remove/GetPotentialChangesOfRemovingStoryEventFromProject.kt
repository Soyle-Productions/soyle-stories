package com.soyle.stories.usecase.storyevent.remove

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.shared.potential.GetPotentialChanges

interface GetPotentialChangesOfRemovingStoryEventFromProject : GetPotentialChanges<RemoveStoryEventFromProject> {

    suspend operator fun invoke(storyEventId: StoryEvent.Id, output: OutputPort)

    fun interface OutputPort {
        suspend fun receivePotentialChanges(response: PotentialChangesOfRemovingStoryEventFromProject)
    }

}