package com.soyle.stories.storyevent.list

import com.soyle.stories.domain.storyevent.StoryEvent
import tornadofx.booleanProperty
import tornadofx.longProperty
import tornadofx.stringProperty

class StoryEventListItemViewModel(val id: StoryEvent.Id) {

    val nameProperty = stringProperty()
    val timeProperty = longProperty(0)

    val prevItemHasSameTime = booleanProperty()
}