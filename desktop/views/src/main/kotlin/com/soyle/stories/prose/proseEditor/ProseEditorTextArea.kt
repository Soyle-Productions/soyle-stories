package com.soyle.stories.prose.proseEditor

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.prose.proseEditor.ProseEditorView.Styles.Companion.mention
import com.soyle.stories.prose.proseEditor.ProseEditorView.Styles.Companion.problem
import com.soyle.stories.prose.proseEditor.ProseEditorView.Styles.Companion.proseEditorTextArea
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.IndexRange
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import org.fxmisc.richtext.GenericStyledArea
import org.fxmisc.richtext.NavigationActions
import org.fxmisc.richtext.TextExt
import org.fxmisc.richtext.model.*
import org.fxmisc.wellbehaved.event.EventPattern
import org.fxmisc.wellbehaved.event.InputMap
import org.fxmisc.wellbehaved.event.Nodes
import tornadofx.*
import java.util.*

class ProseEditorTextArea : GenericStyledArea<Unit, ContentElement, Collection<String>>(
    /* initialParagraphStyle = */Unit,
    /*applyParagraphStyle =  */{ _, _ -> },
    /*initialTextStyle =  */listOf(""),
    /*segmentOps =  */ContentElementOps(),
    /*nodeFactory =  */{
        val segment = it.segment
        TextExt(segment.text).apply {
            if (segment is Mention) {
                toggleClass(mention, segment.issue == null)
                toggleClass(problem, segment.issue != null)
            }
        }
    }
) {

    fun getSegmentContaining(characterIndex: Int): ContentElement?
    {
        var offset = 0
        return content.paragraphs.asSequence()
            .flatMap { it.segments.asSequence() + BasicText("\n") }
            .find {
                val start = offset
                offset += it.text.length
                characterIndex in start .. offset
            }
    }

    fun getElementBounds(element: ContentElement): IndexRange?
    {
        var offset = 0
        val found = content.paragraphs.asSequence()
            .flatMap { it.segments.asSequence() + BasicText("\n") }
            .onEach { offset += it.text.length }
            .any {
                it === element
            }
        return if (found) IndexRange(offset - element.text.length, offset) else null
    }

    fun clearMention(mention: Mention) = replaceMentionElement(mention, BasicText(mention.text))

    fun clearAllMentionsOfEntity(mention: Mention) = replaceAllMentionElements(mention.entityId, BasicText(mention.text))

    fun removeMention(mention: Mention) = replaceMentionElement(mention, BasicText(""))

    fun removeAllMentionsOfEntity(mention: Mention) = replaceAllMentionElements(mention.entityId, BasicText(""))

    fun replaceMention(mention: Mention, with: Mention) = replaceMentionElement(mention, with)

    fun replaceAllMentionsOfEntity(mention: Mention, with: Mention) = replaceAllMentionElements(mention.entityId, with)

    private fun replaceMentionElement(mention: Mention, with: ContentElement)
    {
        val start = findSegment { element, i -> element === mention }?.second
            ?: kotlin.error("could not find mention in document")
        content.replace(
            start,
            start + mention.text.length,
            ReadOnlyStyledDocument.fromSegment(with, Unit, listOf(), ContentElementOps())
        )
    }

    private fun replaceAllMentionElements(entityId: MentionedEntityId<*>, with: ContentElement)
    {
        var offset = 0
        content.replaceMulti(content.paragraphs.asSequence()
            .flatMap { it.segments.asSequence() + BasicText("\n") }
            .onEach { offset += it.text.length }
            .filterIsInstance<Mention>()
            .filter { it.entityId == entityId }
            .map {
                Replacement(offset - it.text.length, offset,
                    ReadOnlyStyledDocument.fromSegment(with, Unit, listOf(), ContentElementOps()))
            }
            .toList().asReversed()
        )
    }

    override fun getContextMenu(): MentionIssueMenu? {
        return super.getContextMenu() as? MentionIssueMenu
    }

    init {
        addClass(proseEditorTextArea)
        preventArrowKeysFromEnteringMentions()
        deleteMentionsAsSingleObjects()
    }

    private fun deleteMentionsAsSingleObjects() {
        val mentionOnLeftOfCaretProperty = SimpleObjectProperty<Mention?>()
        val mentionOnRightOfCaretProperty = SimpleObjectProperty<Mention?>()
        Nodes.addInputMap(this, InputMap.consumeWhen(EventPattern.keyPressed(KeyCode.DELETE), {
            if (selection.length > 0) false
            else {
                mentionOnRightOfCaretProperty.value =
                    findSegment { mention, offset -> offset == caretPosition }?.first
                mentionOnRightOfCaretProperty.value != null
            }
        }) {
            val mentionOnRightOfCaret = mentionOnRightOfCaretProperty.value!!
            deleteText(caretPosition, caretPosition + mentionOnRightOfCaret.text.length)

        })
        Nodes.addInputMap(this, InputMap.consumeWhen(EventPattern.keyPressed(KeyCode.BACK_SPACE), {
            if (selection.length > 0) false
            else {
                mentionOnLeftOfCaretProperty.value =
                    findSegment { mention, offset -> offset + mention.text.length == caretPosition }?.first
                mentionOnLeftOfCaretProperty.value != null
            }
        }) {
            val mentionOnLeftOfCaret = mentionOnLeftOfCaretProperty.value!!
            deleteText(caretPosition - mentionOnLeftOfCaret.text.length, caretPosition)
        })
    }

    private fun preventArrowKeysFromEnteringMentions() {
        // without this function, the arrow keys would only adjust the caret position by a single character length.
        // For any mention with a length greater than 1, it would never jump over due to the caret adjustment code,
        // so, we override the caret movement behavior due to an arrow key

        preventRightArrowFromEnteringMentions()
        preventLeftArrowFromEnteringMentions()
    }

    private fun preventRightArrowFromEnteringMentions() {
        consumeArrowKeysPressed(
            listOf(KeyCode.RIGHT, KeyCode.KP_RIGHT),
            whenMention = { _, offset -> offset == caretPosition },
            insteadMoveTo = { (mention, offset) -> offset + mention.text.length }
        )
    }

    private fun preventLeftArrowFromEnteringMentions() {
        consumeArrowKeysPressed(
            listOf(KeyCode.LEFT, KeyCode.KP_LEFT),
            whenMention = { mention, offset -> offset + mention.text.length == caretPosition },
            insteadMoveTo = { (_, offset) -> offset }
        )
    }

    private fun consumeArrowKeysPressed(
        keyCodes: List<KeyCode>,
        whenMention: (Mention, Int) -> Boolean,
        insteadMoveTo: (Pair<Mention, Int>) -> Int
    ) {
        val foundMentionProperty = SimpleObjectProperty<Pair<Mention, Int>?>(null)
        Nodes.addInputMap(
            this,
            InputMap.consumeWhen(
                EventPattern.anyOf(
                    *keyCodes.map {
                        EventPattern.keyPressed(it, KeyCombination.SHIFT_ANY)
                    }.toTypedArray()
                ),
                {
                    foundMentionProperty.value = findSegment(whenMention)
                    foundMentionProperty.value != null
                }
            ) {
                val mention = foundMentionProperty.value!!
                if (it.isShiftDown) {
                    moveTo(insteadMoveTo(mention), NavigationActions.SelectionPolicy.ADJUST)
                } else {
                    moveTo(insteadMoveTo(mention))
                }
            }
        )
    }

    private fun findSegment(predicate: (Mention, Int) -> Boolean): Pair<Mention, Int>? {
        var offset = 0
        return paragraphs.asSequence()
            .flatMap { it.segments.asSequence() + BasicText("\n") }
            .find {
                if (it !is Mention) {
                    offset += it.text.length
                    return@find false
                }
                val predicateResult = predicate(it, offset)
                if (predicateResult) true
                else {
                    offset += it.text.length
                    false
                }
            }?.let {
                it as Mention to offset
            }
    }
}



