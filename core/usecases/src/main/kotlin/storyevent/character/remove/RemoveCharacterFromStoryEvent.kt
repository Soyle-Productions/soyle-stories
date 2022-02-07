package com.soyle.stories.usecase.storyevent.character.remove

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.character.events.CharacterRemovedFromScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.character.changes.CharacterRemovedFromStoryEvent
import java.util.*

interface RemoveCharacterFromStoryEvent {

	suspend operator fun invoke(storyEventId: StoryEvent.Id, characterId: Character.Id, output: OutputPort)

	interface OutputPort {
		suspend fun characterRemovedFromStoryEvent(characterRemoved: CharacterRemovedFromStoryEvent)
	}

}