package com.soyle.stories.storyevent.item.icon

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.StyleImporter
import javafx.application.Platform
import tornadofx.*

class StoryEventItemIconStyles : Stylesheet() {

    companion object : StyleImporter<StoryEventItemIconStyles>(StoryEventItemIconStyles::class) {

        val storyEventItemIcon by cssclass()

        const val STORY_EVENT_ITEM_ICON_SIZE = 16

    }

    init {
        storyEventItemIcon {
            prefHeight = STORY_EVENT_ITEM_ICON_SIZE.px
            prefWidth = STORY_EVENT_ITEM_ICON_SIZE.px
            minWidth = STORY_EVENT_ITEM_ICON_SIZE.px
            minHeight = STORY_EVENT_ITEM_ICON_SIZE.px
            maxWidth = STORY_EVENT_ITEM_ICON_SIZE.px
            maxHeight = STORY_EVENT_ITEM_ICON_SIZE.px
            shape = "M12 2.02c-5.51 0-9.98 4.47-9.98 9.98s4.47 9.98 9.98 9.98 9.98-4.47 9.98-9.98S17.51 2.02 12 2.02zM11.48 20v-6.26H8L13 4v6.26h3.35L11.48 20z"
            backgroundColor = multi(ColorStyles.lightTextColor)
        }
    }

}