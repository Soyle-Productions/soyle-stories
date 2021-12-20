package com.soyle.stories.domain.storyevent.exceptions

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.DuplicateOperationException

data class DuplicateStoryEventOperationException(
    override val storyEventId: StoryEvent.Id,
    override val message: String
) : DuplicateOperationException(), StoryEventException

internal fun storyEventAlreadyWithoutCoverage(storyEventId: StoryEvent.Id) =
    DuplicateStoryEventOperationException(storyEventId, "$storyEventId already without coverage")

fun StoryEventAlreadyCoveredByScene(storyEventId: StoryEvent.Id, sceneId: Scene.Id) =
    DuplicateStoryEventOperationException(storyEventId, "$storyEventId already covered by $sceneId")