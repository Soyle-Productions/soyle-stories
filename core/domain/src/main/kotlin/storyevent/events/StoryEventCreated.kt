package com.soyle.stories.domain.storyevent.events

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent

data class StoryEventCreated(
    override val storyEventId: StoryEvent.Id,
    val name: String,
    val time: ULong,
    val sceneId: Scene.Id?,
    val projectId: Project.Id
) : StoryEventChange()