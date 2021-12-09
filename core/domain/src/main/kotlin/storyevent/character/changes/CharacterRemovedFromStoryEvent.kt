package com.soyle.stories.domain.storyevent.character.changes

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent

data class CharacterRemovedFromStoryEvent(
    override val storyEventId: StoryEvent.Id,
    override val characterId: Character.Id
) : InvolvedCharacterChange()