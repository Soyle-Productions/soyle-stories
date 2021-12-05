package com.soyle.stories.domain.storyevent.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent

data class CharacterRemovedFromStoryEvent(
    override val storyEventId: StoryEvent.Id,
    val characterId: Character.Id
) : StoryEventChange()