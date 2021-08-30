package com.soyle.stories.storyevent.rename

import com.soyle.stories.domain.storyevent.StoryEvent
import tornadofx.UIComponent

fun interface RenameStoryEventDialog {
    data class Props(val storyEventId: StoryEvent.Id, val currentName: String)
    operator fun invoke(props: Props): UIComponent
}