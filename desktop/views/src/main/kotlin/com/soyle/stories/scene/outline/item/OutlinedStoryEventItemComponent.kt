package com.soyle.stories.scene.outline.item

import com.soyle.stories.storyevent.coverage.UncoverStoryEventController
import javafx.scene.Node

interface OutlinedStoryEventItemComponent {

    fun view(item: OutlinedStoryEventItem): Node

    class Implementation(
        private val uncoverStoryEventController: UncoverStoryEventController
    ) : OutlinedStoryEventItemComponent {

        override fun view(item: OutlinedStoryEventItem): Node {
            val presenter = OutlinedStoryEventItemPresenter(
                item,
                uncoverStoryEventController
            )
            return OutlinedStoryEventItemView(item, presenter)
        }

    }

}