package com.soyle.stories.domain.storyevent.character.changes

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.events.StoryEventChange

abstract class InvolvedCharacterChange : StoryEventChange() {
    abstract val characterId: Character.Id
}