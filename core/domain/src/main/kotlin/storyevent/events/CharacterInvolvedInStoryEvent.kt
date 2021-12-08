package com.soyle.stories.domain.storyevent.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent

data class CharacterInvolvedInStoryEvent(
    override val storyEventId: StoryEvent.Id,
    val characterId: Character.Id,
    val characterName: String
) : StoryEventChange()