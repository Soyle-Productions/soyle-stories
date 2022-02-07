package com.soyle.stories.usecase.storyevent.character.involve

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.character.changes.CharacterInvolvedInStoryEvent

interface InvolveCharacterInStoryEvent {

	suspend operator fun invoke(storyEventId: StoryEvent.Id, characterId: Character.Id, output: OutputPort)

	fun interface OutputPort {
		suspend fun characterInvolvedInStoryEvent(characterInvolved: CharacterInvolvedInStoryEvent)
	}

}