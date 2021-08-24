package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.str
import com.soyle.stories.domain.validation.NonBlankString

fun makeStoryEvent(
    id: StoryEvent.Id = StoryEvent.Id(),
    name: NonBlankString = storyEventName(),
    time: Long = storyEventTime(),
    projectId: Project.Id = Project.Id(),
    previousStoryEventId: StoryEvent.Id? = null,
    nextStoryEventId: StoryEvent.Id? = null,
    linkedLocationId: Location.Id? = null,
    includedCharacterIds: List<Character.Id> = listOf()
) = StoryEvent(
    id,
    name,
    time,
    projectId,
    previousStoryEventId,
    nextStoryEventId,
    linkedLocationId,
    includedCharacterIds
)

fun storyEventName() = nonBlankStr("Story Event ${str()}")

fun storyEventTime() = (0L .. 100L).random()