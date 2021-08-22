package com.soyle.stories.desktop.view.storyevent.list

import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.access
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.list.creationButton.StoryEventListTool
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import tornadofx.Stylesheet.Companion.cell

class `Story Event List Tool Assertions` private constructor(private val access: StoryEventListToolAccess) {
    companion object {
        fun StoryEventListTool.assertThis(op: `Story Event List Tool Assertions`.() -> Unit) {
            `Story Event List Tool Assertions`(access()).op()
        }
    }

    fun hasStoryEvent(storyEvent: StoryEvent) {
        val cell = access.storyEventListCells.find { it.item.id == storyEvent.id.uuid.toString() }
            ?: fail("Story Event List does not have an item matching the id ${storyEvent.id}")
        assertEquals(storyEvent.name, cell.text)
    }

}