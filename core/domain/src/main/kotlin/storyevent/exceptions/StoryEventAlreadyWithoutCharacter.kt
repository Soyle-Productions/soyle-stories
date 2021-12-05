package com.soyle.stories.domain.storyevent.exceptions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.DuplicateOperationException

data class StoryEventAlreadyWithoutCharacter(val storyEventId: StoryEvent.Id, val characterId: Character.Id) :
    DuplicateOperationException(), StoryEventException