package com.soyle.stories.desktop.view.scene.sceneCharacters.selectStoryEvent

import com.soyle.stories.scene.characters.include.selectStoryEvent.SelectStoryEventPromptView
import org.junit.jupiter.api.Assertions.*

class `Select Story Event Prompt Assertions` (private val view: SelectStoryEventPromptView) {

    fun hasNoStoryEvents() {
        val storyEventItems = view.access().storyEventItems
        assertTrue(storyEventItems.isEmpty()) {
            "Expected no story events.  Found $storyEventItems"
        }
    }

    fun hasStoryEventWithName(name: String) {
        val item = view.access().storyEventItems.find { it.text == name }
        assertNotNull(item) {
            """
                Expected to find story event item named "$name"
                Items: ${view.access().storyEventItems.map { it.text }}
            """.trimIndent()
        }
    }

    fun hasStoryEventsNamed(names: List<String>) {
        val itemNames = view.access().storyEventItems.map { it.text }
        val itemNameSet = itemNames.toSet()
        val notFound = names.filterNot { it in itemNameSet }
        if (notFound.isNotEmpty()) {
            fail<Nothing>("""
                Did not find the names: $notFound
                Items: $itemNames
            """.trimIndent())
        }
    }

}

fun assertThat(view: SelectStoryEventPromptView, assertions: `Select Story Event Prompt Assertions`.() -> Unit) {
    `Select Story Event Prompt Assertions`(view).assertions()
}