package com.soyle.stories.desktop.view.prose.proseEditor

import com.soyle.stories.common.PairOf
import com.soyle.stories.entities.MentionedEntityId
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
        assertEquals(expectedContent, driver.getContent()) { "Prose editor content does not match" }
    }

    fun containsContent(expectedContent: String)
    {
        assertTrue(driver.getContent().contains(expectedContent)) { "Prose editor content does not contain $expectedContent" }
    }

    fun doesNotContainContent(unexpectedContent: String)
    {
        assertFalse(driver.getContent().contains(unexpectedContent)) { "Prose editor content contains \"$unexpectedContent\"" }
    }

    fun hasMention(entityId: MentionedEntityId<*>, position: ProseMentionRange)
    {
        val mention = driver.getMentionAt(position.index, position.index + position.length)!!
        assertEquals(entityId, mention.entityId)
    }

    fun hasIssueWithMention(entityId: MentionedEntityId<*>, position: ProseMentionRange)
    {
        val mention = driver.getMentionAt(position.index, position.index + position.length)!!
        assertEquals(entityId, mention.entityId)
        assertNotNull(mention.issue) { "Mention does not have an issue" }
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
        assertEquals(expectedLabel, storyElement!!.name.toString())
        assertEquals(expectedType, storyElement.type)
    }

    fun isListingAllStoryElementsInOrder(elements: List<PairOf<String>>)
    {
        elements.forEachIndexed { index, pair -> isListingStoryElement(index, pair.first, pair.second) }
    }

    fun isDisabled()
    {
        assertTrue(driver.textArea.isDisabled)
    }

    fun eachSuggestedMention(assertions: MentionSuggestionAssertions.() -> Unit) {
        driver.mentionMenuItems.forEach {
            MentionSuggestionAssertions()
        }
    }

    fun isShowingMentionIssueMenuForMention(mentionText: String)
    {
        assertTrue(driver.isShowingMentionIssueMenu()) { "Mention Issue menu is not showing at all" }
        assertTrue(driver.mentionIssueMenuIsRelatedToMention(mentionText)) { "Mention Issue menu is not showing for $mentionText" }
    }

    fun mentionIssueMenuHasOption(expectedOption: String)
    {
        assertNotNull(driver.mentionIssueMenu!!.items.find { it.text == expectedOption })
    }

    fun mentionIssueReplacementMenuHasOption(expectedOption: String)
    {
        assertNotNull(with(driver) {
            mentionIssueMenu!!.replacementOption()!!.items.find { it.text == expectedOption }
        })
    }

    fun doesNotHaveAnyReplacementMentionElementsListed()
    {
        assertTrue(with(driver) {
            mentionIssueMenu?.replacementOption()
        }?.items?.drop(1)?.isEmpty() != false)
    }

    fun isListingAllReplacementOptionsInOrder(expectedLabels: List<String>)
    {
        assertEquals(expectedLabels, with(driver) {
            mentionIssueMenu?.replacementOption()
        }?.items?.drop(1)?.map { it.text })
    }

    class MentionSuggestionAssertions internal constructor() {


    }
}