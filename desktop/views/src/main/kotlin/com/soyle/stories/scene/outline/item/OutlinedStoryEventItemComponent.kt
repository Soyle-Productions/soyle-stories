package com.soyle.stories.scene.outline.item

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.coverage.uncover.ConfirmUncoverStoryEventPrompt
import com.soyle.stories.storyevent.coverage.uncover.UncoverStoryEventController
import com.soyle.stories.storyevent.coverage.uncover.UncoverStoryEventRamificationsReport
import javafx.scene.Node

interface OutlinedStoryEventItemComponent {

    fun view(item: OutlinedStoryEventItem): Node

    class Implementation(
        val makePrompt: () -> ConfirmUncoverStoryEventPrompt,
        val makeReport: (StoryEvent.Id) -> UncoverStoryEventRamificationsReport,
        private val uncoverStoryEventController: UncoverStoryEventController
    ) : OutlinedStoryEventItemComponent {

        override fun view(item: OutlinedStoryEventItem): Node {
            val presenter = OutlinedStoryEventItemPresenter(
                item,
                makePrompt,
                makeReport,
                uncoverStoryEventController
            )
            return OutlinedStoryEventItemView(item, presenter)
        }

    }

}