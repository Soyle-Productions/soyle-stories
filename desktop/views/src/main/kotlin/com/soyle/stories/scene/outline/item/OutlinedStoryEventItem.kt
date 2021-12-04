package com.soyle.stories.scene.outline.item

import com.soyle.stories.domain.storyevent.StoryEvent
import javafx.beans.binding.StringExpression
import tornadofx.getValue
import tornadofx.setValue
import tornadofx.stringProperty

class OutlinedStoryEventItem(
    val storyEventId: StoryEvent.Id
) {

    private val nameProperty = stringProperty()
    fun name(): StringExpression = nameProperty
    var name: String by nameProperty

}