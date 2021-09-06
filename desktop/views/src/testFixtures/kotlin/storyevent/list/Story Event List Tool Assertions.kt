package com.soyle.stories.desktop.view.storyevent.list

import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.access
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.list.StoryEventListToolView
import org.junit.jupiter.api.Assertions.*

class `Story Event List Tool Assertions` private constructor(private val access: StoryEventListToolAccess) {
    companion object {
        fun StoryEventListToolView.assertThis(op: `Story Event List Tool Assertions`.() -> Unit) {
            `Story Event List Tool Assertions`(access()).op()
        }
    }

    fun hasStoryEvent(storyEvent: StoryEvent) {
        val item = access.storyEventItems.find { it.id == storyEvent.id }
            ?: fail("Story Event List does not have an item matching the id ${storyEvent.id}")
        assertEquals(storyEvent.name, item.nameProperty.value)
        assertEquals(storyEvent.time, item.timeProperty.value)
    }

    fun doesNotHaveStoryEventNamed(name: String) {
        val item = access.storyEventItems.find { it.nameProperty.value == name }
        assertNull(item)
    }

}