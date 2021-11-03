package com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label.menu

import com.soyle.stories.desktop.adapter.storyevent.AdjustStoryEventsTimeControllerDouble
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponent

class TimelineRulerLabelMenuComponentDouble(
    val dependencies: TimelineRulerLabelMenuComponent.Dependencies = Dependencies()
) : TimelineRulerLabelMenuComponent by TimelineRulerLabelMenuComponent.Implementation(dependencies) {

    class Dependencies(
        override val adjustStoryEventsTimeController: AdjustStoryEventsTimeController = AdjustStoryEventsTimeControllerDouble()
    ) : TimelineRulerLabelMenuComponent.Dependencies

}