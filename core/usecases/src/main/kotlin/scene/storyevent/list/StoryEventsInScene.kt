package com.soyle.stories.usecase.scene.storyevent.list

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventItem

class StoryEventsInScene(
    val sceneId: Scene.Id,
    list: List<StoryEventItem>) : List<StoryEventItem> by list {

    constructor(sceneId: Scene.Id, vararg items: StoryEventItem) : this(sceneId, items.toList())

}