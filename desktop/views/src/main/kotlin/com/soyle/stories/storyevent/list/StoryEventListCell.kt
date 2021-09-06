package com.soyle.stories.storyevent.list

import javafx.scene.control.ListCell

interface StoryEventListCell {
    operator fun invoke(): ListCell<StoryEventListItemViewModel>
}