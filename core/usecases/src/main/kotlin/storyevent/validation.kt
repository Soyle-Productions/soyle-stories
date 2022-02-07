package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.validation.EntityNotFoundException
import java.util.*

data class StoryEventDoesNotExist(val storyEventId: UUID) : EntityNotFoundException(storyEventId)
class CharacterNotInStoryEvent(val storyEventId: UUID, val characterId: UUID) : EntityNotFoundException(characterId)