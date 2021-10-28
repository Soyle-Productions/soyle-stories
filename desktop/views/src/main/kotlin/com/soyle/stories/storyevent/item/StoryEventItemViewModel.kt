package com.soyle.stories.storyevent.item

import com.soyle.stories.domain.storyevent.StoryEvent
import javafx.beans.binding.LongExpression
import javafx.beans.binding.StringExpression

interface StoryEventItemViewModel {

    val storyEventId: StoryEvent.Id

    val name: String
    val time: Long

}