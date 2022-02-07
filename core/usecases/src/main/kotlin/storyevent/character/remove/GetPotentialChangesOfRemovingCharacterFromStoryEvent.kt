package com.soyle.stories.usecase.storyevent.character.remove

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.shared.potential.GetPotentialChanges

interface GetPotentialChangesOfRemovingCharacterFromStoryEvent : GetPotentialChanges<RemoveCharacterFromStoryEvent> {
    suspend operator fun invoke(storyEventId: StoryEvent.Id, characterId: Character.Id, output: OutputPort)

    fun interface OutputPort {
        suspend fun receivePotentialChanges(response: PotentialChangesOfRemovingCharacterFromStoryEvent)
    }

}