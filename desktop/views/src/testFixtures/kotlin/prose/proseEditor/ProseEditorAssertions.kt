package com.soyle.stories.prose.proseEditor

import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorDriver
import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.prose.ProseMentionRange
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

    fun hasMention(entityId: MentionedEntityId<*>, startIndex: Int, endIndex: Int)
    {
        val mention = driver.getMentionAt(startIndex, endIndex)!!
        assertEquals(entityId, mention.entityId)
    }

    fun hasIssueWithMention(entityId: MentionedEntityId<*>, startIndex: Int, endIndex: Int)
    {
        val mention = driver.getMentionAt(startIndex, endIndex)!!
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

    fun isListingStoryElement(index: Int, expectedLabel: String, expectedType: String, expectedAddendum: String? = null)
    {
        val storyElement = driver.listedStoryElementAt(index)
        assertEquals(expectedLabel, storyElement!!.name.toString()) { "Item $index does not match expected label" }
        assertEquals(expectedAddendum, storyElement.addendum?.toString()) { "Item $index does not match expected addendum" }
        assertEquals(expectedType, storyElement.type) { "Item $index does not match expected type" }
    }

    fun isListingAllStoryElementsInOrder(elements: List<Triple<String, String, String?>>)
    {
        elements.forEachIndexed { index, pair -> isListingStoryElement(index, pair.first, pair.second, pair.third) }
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
        }) { "Could not find replacement option with name $expectedOption" }
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