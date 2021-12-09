package com.soyle.stories.domain.storyevent.character.exceptions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.exceptions.StoryEventException

interface InvolvedCharacterException : StoryEventException {
    val characterId: Character.Id
}