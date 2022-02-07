package com.soyle.stories.desktop.view.storyevent.list

import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.access
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.list.StoryEventListToolView
import org.junit.jupiter.api.Assertions.*
import kotlin.math.exp

class `Story Event List Tool Assertions` private constructor(private val access: StoryEventListToolAccess) {
    companion object {
        fun StoryEventListToolView.assertThis(op: `Story Event List Tool Assertions`.() -> Unit) {
            `Story Event List Tool Assertions`(access()).op()
        }
    }

    fun hasNoStoryEvents() {
        val items = access.storyEventItems
        assertTrue(items.isEmpty()) { "Expected ${items}\n to be empty." }
    }

    fun isShowingWelcomePrompt() {
        assertNotNull(access.welcomeMessage) { "Expected to find welcome message" }
        assertNotNull(access.createStoryEventButton) { "Expected to find create story event button" }
    }

    fun hasStoryEvent(storyEvent: StoryEvent) {
        val item = access.storyEventItems.find { it.id == storyEvent.id }
            ?: fail("Story Event List does not have an item matching the id ${storyEvent.id}")
        assertEquals(storyEvent.name, item.nameProperty.value)
        assertEquals(storyEvent.time.toLong(), item.timeProperty.value)
    }

    fun doesNotHaveStoryEventNamed(name: String) {
        val item = access.storyEventItems.find { it.nameProperty.value == name }
        assertNull(item)
    }

    fun hasOrder(expectedOrder: List<String>) {
        val actual = access.storyEventItems.map { it.nameProperty.value }
        assertEquals(
            expectedOrder,
            actual
        ) {
            """
                Expected the following order of story events:
                    Expected: $expectedOrder
                      Actual: $actual
            """.trimIndent()
        }
    }

}