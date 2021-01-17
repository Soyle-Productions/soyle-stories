package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.di.resolve
import com.soyle.stories.prose.proseEditor.ProseEditorTextArea.Styles.Companion.proseEditorTextArea
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Priority
import org.fxmisc.richtext.NavigationActions
import org.fxmisc.wellbehaved.event.EventPattern
import org.fxmisc.wellbehaved.event.InputMap
import org.fxmisc.wellbehaved.event.Nodes
import tornadofx.*
import java.util.*

class ProseEditorView : Fragment() {

    private val viewListener = resolve<ProseEditorViewListener>()
    private val state = resolve<ProseEditorState>()

    private val textArea = ProseEditorTextArea()
    private val mentionMenu = MatchingStoryElementsPopup(scope, state.mentionQueryState)
    private val mentionIssueMenu = ContextMenu()
    private fun mentionIssueItems(mention: Mention) = listOf(
        clearMentionOption(mention),
        clearAllMentionOption(mention),
        removeMentionOption(mention),
        removeAllMentionsOption(mention)
    )

    override val root = hbox {
        addClass(Styles.proseEditor)
        add(textArea)
        textArea.apply {
            hgrow = Priority.ALWAYS
            contextMenu = mentionIssueMenu
            isWrapText = true // css wraptext property does not work with richtextfx
        }
    }

    private val viewController = ProseEditorViewController(
        viewListener,
        state,
        textArea.caretSelectionBind::displaceCaret,
        { textArea.caretSelectionBind.underlyingSelection.selectRange(it.first, it.last) }
    )

    init {
        with(textArea) {
            disableWhen(state.isLocked)
            onLoseFocus(viewController::onTextAreaFocusLost)
            val onOutsideSelectionMousePressed = this.onOutsideSelectionMousePressed
            setOnOutsideSelectionMousePressed {
                viewController.onClickInNewArea()
                onOutsideSelectionMousePressed.handle(it)
            }
            caretPositionProperty().onChange { viewController.onCaretMoved(it) }
            caretSelectionBind.underlyingSelection.rangeProperty().onChange {
                if (it == null) return@onChange
                viewController.onSelectionChanged(it.start..it.end)
            }
            setOnKeyTyped {
                viewController.onKeyTyped(it.character, caretPosition - 1)
            }
            setOnContextMenuRequested {
                val characterIndex: Int = if (it.isKeyboardTrigger) caretPosition
                else {
                    val optionalIndex = hit(it.x, it.y).characterIndex
                    if (optionalIndex.isEmpty) return@setOnContextMenuRequested
                    else optionalIndex.asInt
                }
                val segment = findSegment { mention, i -> characterIndex in i..i + mention.text.length }
                if (segment?.first is Mention && segment.first.issue != null) {
                    mentionIssueMenu.properties["relative-mention"] = segment.first
                    mentionIssueMenu.items.setAll(mentionIssueItems(segment.first))
                    it.consume()
                    if (it.isKeyboardTrigger) {
                        val screenPosition =
                            getCharacterBoundsOnScreen(segment.second, segment.second + segment.first.text.length).get()
                        mentionIssueMenu.show(this, screenPosition.minX, screenPosition.maxY)
                    } else {
                        mentionIssueMenu.show(this, it.screenX, it.screenY)
                    }
                } else {
                    mentionIssueMenu.properties["relative-mention"] = null
                    mentionIssueMenu.items.clear()
                }
            }
        }
    }

    init {
        with(mentionMenu) {
            isAutoHide = true
            setOnAutoHide { viewListener.cancelQuery() }

            state.mentionQueryState.onChange {
                when (it) {
                    is TriggeredQuery -> if (!isShowing) {
                        textArea.getCharacterBoundsOnScreen(it.primedIndex, it.primedIndex + 1)
                            .ifPresent { bounds ->
                                // TODO calculate the actual distance between the first character in the first item in the listview to the edge of the popup
                                x = bounds.minX - 8.0
                                y = bounds.maxY
                                show(this@ProseEditorView.currentWindow)
                            }
                    }
                    else -> if (isShowing) hide()
                }
            }
        }
    }

    init {
        properties["mentionMenu"] = mentionMenu

        var updateCausedByInput = false
        var pushingUpdate = false

        state.content.onChange { elements: ObservableList<ContentElement>? ->
            if (updateCausedByInput) return@onChange
            pushingUpdate = true
            val currentPosition = textArea.caretPosition
            textArea.clear()
            elements?.forEach {
                textArea.append(it, listOf())
            }
            textArea.moveTo(currentPosition.coerceAtMost(textArea.text.length).coerceAtLeast(0))
            pushingUpdate = false
        }
        textArea.textProperty().onChange {
            if (pushingUpdate) return@onChange
            updateCausedByInput = true
            state.content.setAll(textArea.paragraphs.flatMap { it.segments })
            updateCausedByInput = false
        }
        textArea.moveTo(0)
        textArea.requestFollowCaret()
        preventArrowKeysFromEnteringMentions()

        val mentionOnLeftOfCaretProperty = SimpleObjectProperty<Mention?>()
        val mentionOnRightOfCaretProperty = SimpleObjectProperty<Mention?>()
        Nodes.addInputMap(textArea, InputMap.consumeWhen(EventPattern.keyPressed(KeyCode.DELETE), {
            if (textArea.selection.length > 0) false
            else {
                mentionOnRightOfCaretProperty.value =
                    findSegment { mention, offset -> offset == textArea.caretPosition }?.first
                mentionOnRightOfCaretProperty.value != null
            }
        }) {
            val mentionOnRightOfCaret = mentionOnRightOfCaretProperty.value!!
            textArea.deleteText(textArea.caretPosition, textArea.caretPosition + mentionOnRightOfCaret.text.length)

        })
        Nodes.addInputMap(textArea, InputMap.consumeWhen(EventPattern.keyPressed(KeyCode.BACK_SPACE), {
            if (textArea.selection.length > 0) false
            else {
                mentionOnLeftOfCaretProperty.value =
                    findSegment { mention, offset -> offset + mention.text.length == textArea.caretPosition }?.first
                mentionOnLeftOfCaretProperty.value != null
            }
        }) {
            val mentionOnLeftOfCaret = mentionOnLeftOfCaretProperty.value!!
            textArea.deleteText(textArea.caretPosition - mentionOnLeftOfCaret.text.length, textArea.caretPosition)
        })

        viewListener.getValidState()
    }

    private fun clearMentionOption(mention: Mention): MenuItem {
        return MenuItem("Clear this Mention of ${mention.text} and Use Normal Text").apply {
            id = "clear-mention"
            action { viewListener.clearMention(mention) }
        }
    }

    private fun clearAllMentionOption(mention: Mention): MenuItem {
        return MenuItem("Clear all Mentions of ${mention.text} and Use Normal Text").apply {
            id = "clear-mention"
            action { viewListener.clearAllMentionsOfEntity(mention.entityId) }
        }
    }

    private fun removeMentionOption(mention: Mention): MenuItem {
        return MenuItem("Remove this Mention of ${mention.text} and Remove the Text").apply {
            id = "remove-mention"
            action {
                viewListener.removeMention(mention)
            }
        }
    }

    private fun removeAllMentionsOption(mention: Mention): MenuItem {
        return MenuItem("Remove all Mentions of ${mention.text} and Remove the Text").apply {
            id = "remove-mention"
            action {
                viewListener.removeAllMentionsOfEntity(mention.entityId)
            }
        }
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
            whenMention = { _, offset -> offset == textArea.caretPosition },
            insteadMoveTo = { (mention, offset) -> offset + mention.text.length }
        )
    }

    private fun preventLeftArrowFromEnteringMentions() {
        consumeArrowKeysPressed(
            listOf(KeyCode.LEFT, KeyCode.KP_LEFT),
            whenMention = { mention, offset -> offset + mention.text.length == textArea.caretPosition },
            insteadMoveTo = { (_, offset) -> offset }
        )
    }

    private fun consumeArrowKeysPressed(
        keyCodes: List<KeyCode>,
        whenMention: (Mention, Int) -> Boolean,
        insteadMoveTo: (Pair<Mention, Int>) -> Int
    ) {
        Nodes.addInputMap(
            textArea,
            InputMap.consumeWhen(
                EventPattern.anyOf(
                    *keyCodes.map {
                        EventPattern.keyPressed(it, KeyCombination.SHIFT_ANY)
                    }.toTypedArray()
                ),
                { findSegment(whenMention) != null }
            ) {
                val mention = findSegment(whenMention)!!
                if (it.isShiftDown) {
                    textArea.moveTo(insteadMoveTo(mention), NavigationActions.SelectionPolicy.ADJUST)
                } else {
                    textArea.moveTo(insteadMoveTo(mention))
                }
            }
        )
    }

    private fun findSegment(predicate: (Mention, Int) -> Boolean): Pair<Mention, Int>? {
        var offset = 0
        return textArea.paragraphs.asSequence()
            .flatMap { it.segments.asSequence() }
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

    class Styles : Stylesheet() {
        companion object {

            val proseEditor by cssclass()

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            proseEditor {
                fillHeight = true
                padding = box(32.px)

                proseEditorTextArea {
                    padding = box(32.px)
                }
            }
        }
    }

}
