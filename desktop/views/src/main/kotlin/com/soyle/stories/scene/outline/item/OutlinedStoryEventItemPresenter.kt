package com.soyle.stories.scene.outline.item

import com.soyle.stories.storyevent.coverage.UncoverStoryEventController

class OutlinedStoryEventItemPresenter(
    private val item: OutlinedStoryEventItem,
    private val uncoverStoryEventController: UncoverStoryEventController
) : OutlinedStoryEventItemActions {

    override fun remove() {
        uncoverStoryEventController.uncoverStoryEvent(item.storyEventId)
    }

}