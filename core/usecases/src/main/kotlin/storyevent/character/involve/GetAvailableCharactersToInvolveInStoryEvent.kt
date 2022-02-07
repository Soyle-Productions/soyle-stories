package com.soyle.stories.usecase.storyevent.character.involve

import com.soyle.stories.domain.storyevent.StoryEvent

interface GetAvailableCharactersToInvolveInStoryEvent {

    suspend operator fun invoke(storyEventId: StoryEvent.Id, output: OutputPort): Throwable?

    fun interface OutputPort {
        suspend fun receiveAvailableCharacters(availableCharacters: AvailableCharactersToInvolveInStoryEvent)
    }

}