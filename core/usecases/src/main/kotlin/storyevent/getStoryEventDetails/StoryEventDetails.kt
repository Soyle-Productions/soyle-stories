package com.soyle.stories.usecase.storyevent.getStoryEventDetails

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.storyevent.StoryEvent
import java.util.*

class StoryEventDetails(
    val storyEvent: StoryEvent.Id,
    val name: String,
    val location: StoryEventLocation?,
    val includedCharacters: List<StoryEventCharacter>
)

class StoryEventLocation(
    val location: Location.Id,
    val name: String
)

class StoryEventCharacter(
    val character: Character.Id,
    val name: String
)