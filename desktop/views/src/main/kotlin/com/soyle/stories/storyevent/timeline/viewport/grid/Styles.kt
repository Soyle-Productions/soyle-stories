package com.soyle.stories.storyevent.timeline.viewport.grid

import com.soyle.stories.common.StyleImporter
import com.soyle.stories.storyevent.item.icon.StoryEventItemIconStyles
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelStyles
import tornadofx.Stylesheet
import tornadofx.cssclass

class TimelineViewPortGridStyles : Stylesheet() {

    companion object : StyleImporter<TimelineViewPortGridStyles>(TimelineViewPortGridStyles::class) {

        const val ROW_V_PADDING = 4.0

        const val ROW_HEIGHT = ROW_V_PADDING +
                StoryPointLabelStyles.STORY_POINT_V_PADDING +
                StoryEventItemIconStyles.STORY_EVENT_ITEM_ICON_SIZE +
                StoryPointLabelStyles.STORY_POINT_V_PADDING +
                ROW_V_PADDING

        const val LABEL_SPACING = 8.0

        const val COLLAPSED_LABEL_WIDTH: Double = 0.0 +
                StoryPointLabelStyles.STORY_POINT_H_PADDING +
                StoryEventItemIconStyles.STORY_EVENT_ITEM_ICON_SIZE +
                StoryPointLabelStyles.STORY_POINT_H_PADDING

        val timelineViewPortGrid by cssclass()

    }

    init {

    }

}