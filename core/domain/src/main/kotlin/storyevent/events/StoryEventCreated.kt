package com.soyle.stories.domain.storyevent.events

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent

class StoryEventCreated(
    override val storyEventId: StoryEvent.Id,
    val name: String,
    val time: Long,
    val projectId: Project.Id
) : StoryEventChange()