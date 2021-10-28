package com.soyle.stories.desktop.view.storyevent.item

import com.soyle.stories.desktop.adapter.storyevent.AdjustStoryEventsTimeControllerDouble
import com.soyle.stories.desktop.adapter.storyevent.RemoveStoryEventControllerDouble
import com.soyle.stories.desktop.adapter.storyevent.RenameStoryEventControllerDouble
import com.soyle.stories.storyevent.item.*
import com.soyle.stories.storyevent.remove.RemoveStoryEventController
import com.soyle.stories.storyevent.rename.RenameStoryEventController
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventControllerDouble
import javafx.scene.control.ContextMenu

class StoryEventItemMenuComponentDouble(
    private val dependencies: StoryEventItemMenuComponent.Dependencies = Dependencies()
) : StoryEventItemMenuComponent {

    class Dependencies(
        override val renameStoryEventController: RenameStoryEventController = RenameStoryEventControllerDouble(),
        override val rescheduleStoryEventController: RescheduleStoryEventController = RescheduleStoryEventControllerDouble(),
        override val adjustStoryEventsTimeController: AdjustStoryEventsTimeController = AdjustStoryEventsTimeControllerDouble(),
        override val removeStoryEventController: RemoveStoryEventController = RemoveStoryEventControllerDouble()
    ) : StoryEventItemMenuComponent.Dependencies

    override fun StoryEventItemMenu(selection: StoryEventItemSelection): ContextMenu {
        return StoryEventItemMenuComponent.Implementation(dependencies).StoryEventItemMenu(selection)
    }
}