package com.soyle.stories.desktop.view.prose.proseEditor

import com.soyle.stories.common.EntityId
import com.soyle.stories.entities.ProseMentionRange
import com.soyle.stories.prose.proseEditor.ProseEditorView
import org.junit.jupiter.api.Assertions.*

class ProseEditorAssertions private constructor(private val driver: ProseEditorDriver) {
    companion object {
        fun assertThat(proseEditor: ProseEditorView, assertions: ProseEditorAssertions.() -> Unit) {
            ProseEditorAssertions(ProseEditorDriver(proseEditor)).assertions()
        }
    }

    fun hasContent(expectedContent: String)
    {
        assertEquals(expectedContent, driver.getContent())
    }

    fun hasMention(entityId: EntityId<*>, position: ProseMentionRange)
    {
        val mention = driver.getMentionAt(position.index, position.index + position.length)!!
        assertEquals(entityId, mention)
    }

    fun suggestedMentionListIsVisible() {
        assertTrue(driver.isShowingMentionMenu())
    }
    fun suggestedMentionListIsNotVisible() {
        assertFalse(driver.isShowingMentionMenu())
    }
    fun mentionListIsAlignedWithCharacterAt(characterIndex: Int) {
        assertEquals(characterIndex, driver.mentionMenuCharacterAlignment())
    }

    fun isListingStoryElement(index: Int, expectedLabel: String, expectedType: String)
    {
        val storyElement = driver.listedStoryElementAt(index)
        assertEquals(expectedLabel, storyElement!!.name)
        assertEquals(expectedType, storyElement.type)
    }
}