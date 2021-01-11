package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.di.resolve
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.Parent
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
        .apply {
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

    private val viewController = ProseEditorViewController(
        viewListener,
        state,
        textArea.caretSelectionBind::displaceCaret,
        { textArea.caretSelectionBind.underlyingSelection.selectRange(it.first, it.last) }
    )

    override val root: Parent = hbox {
        isFillHeight = true
        style {
            padding = box(32.px)
        }
        add(textArea)
        addClass("prose-editor")
        textArea.apply {
            hgrow = Priority.ALWAYS
            style {
                padding = box(32.px)
            }
            isWrapText = true
            disableWhen(state.isLocked)
            onLoseFocus(viewController::onTextAreaFocusLost)
            val onOutsideSelectionMousePressed = this.onOutsideSelectionMousePressed
            setOnOutsideSelectionMousePressed {
                viewController.onClickInNewArea()
                onOutsideSelectionMousePressed.handle(it)
            }
            textArea.caretPositionProperty().onChange { viewController.onCaretMoved(it) }
            textArea.caretSelectionBind.underlyingSelection.rangeProperty().onChange {
                if (it == null) return@onChange
                viewController.onSelectionChanged(it.start..it.end)
            }
            textArea.setOnKeyTyped {
                viewController.onKeyTyped(it.character, textArea.caretPosition - 1)
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

}
