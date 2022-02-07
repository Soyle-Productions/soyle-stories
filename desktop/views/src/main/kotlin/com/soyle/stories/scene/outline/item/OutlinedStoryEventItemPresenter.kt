package com.soyle.stories.scene.outline.item

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.coverage.uncover.ConfirmUncoverStoryEventPrompt
import com.soyle.stories.storyevent.coverage.uncover.UncoverStoryEventController
import com.soyle.stories.storyevent.coverage.uncover.UncoverStoryEventRamificationsReport

class OutlinedStoryEventItemPresenter(
    private val item: OutlinedStoryEventItem,
    private val makePrompt: () -> ConfirmUncoverStoryEventPrompt,
    private val makeReport: (StoryEvent.Id) -> UncoverStoryEventRamificationsReport,
    private val uncoverStoryEventController: UncoverStoryEventController
) : OutlinedStoryEventItemActions {

    override fun remove() {
        uncoverStoryEventController.uncoverStoryEvent(
            item.storyEventId,
            makePrompt(),
            makeReport(item.storyEventId)
        )
    }

}