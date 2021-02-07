package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.str

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