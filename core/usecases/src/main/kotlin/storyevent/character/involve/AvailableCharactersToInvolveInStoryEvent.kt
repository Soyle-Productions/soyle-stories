package com.soyle.stories.usecase.storyevent.character.involve

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.shared.availability.AvailableItems
import com.soyle.stories.usecase.shared.availability.AvailableStoryElementItem

class AvailableCharactersToInvolveInStoryEvent(
    val storyEvent: StoryEvent.Id,
    public override val allAvailableElements: Set<AvailableStoryElementItem<Character.Id>>
) : AvailableItems<Character.Id>()