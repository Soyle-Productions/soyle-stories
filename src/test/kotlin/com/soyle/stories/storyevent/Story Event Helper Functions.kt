package com.soyle.stories.storyevent

import com.soyle.stories.common.str
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.StoryEvent

fun makeStoryEvent(
    id: StoryEvent.Id = StoryEvent.Id(),
    name: String = "Story Event ${str()}",
    projectId: Project.Id = Project.Id(),
    previousStoryEventId: StoryEvent.Id? = null,
    nextStoryEventId: StoryEvent.Id? = null,
    linkedLocationId: Location.Id? = null,
    includedCharacterIds: List<Character.Id> = listOf()
) = StoryEvent(
    id,
    name,
    projectId,
    previousStoryEventId,
    nextStoryEventId,
    linkedLocationId,
    includedCharacterIds
)