package com.soyle.stories.usecase.storyevent

import com.soyle.stories.usecase.character.CharacterDoesNotExist
import org.junit.jupiter.api.Assertions
import java.util.*

fun storyEventDoesNotExist(storyEventId: UUID) = { actual: Any? ->
	actual as StoryEventDoesNotExist
	Assertions.assertEquals(storyEventId, actual.storyEventId)
}

fun characterNotInStoryEvent(storyEventId: UUID, characterId: UUID) = { actual: Any? ->
	actual as CharacterNotInStoryEvent
	Assertions.assertEquals(storyEventId, actual.storyEventId)
	Assertions.assertEquals(characterId, actual.characterId)
}

fun characterDoesNotExist(characterId: UUID) = { actual: Any? ->
	actual as CharacterDoesNotExist
	Assertions.assertEquals(characterId, actual.characterId)
}