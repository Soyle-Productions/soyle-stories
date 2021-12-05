package com.soyle.stories.domain.storyevent.events

import com.soyle.stories.domain.storyevent.StoryEvent

data class CharacterInvolvedInStoryEvent(override val storyEventId: StoryEvent.Id) : StoryEventChange()