package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.exists
import com.soyle.stories.di.resolve
import com.soyle.stories.entities.ProseMention
import com.soyle.stories.soylestories.Styles
import javafx.collections.ObservableList
import javafx.scene.Parent
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Popup
import org.fxmisc.richtext.StyleClassedTextArea
import tornadofx.*

class ProseEditorView : Fragment() {

    private val viewListener = resolve<ProseEditorViewListener>()
    private val state = resolve<ProseEditorState>()

    private val textArea = StyleClassedTextArea()
    private val mentionMenu = Popup().apply {
        val vbox = VBox().apply {
            style {
                backgroundColor += Color.WHITE
                borderColor += box(Styles.Purple)
                borderWidth += box(1.px)
            }
            label("Loading...") {
                state.mentionQueryState.onChange {
                    exists = it is MentionQueryLoading
                }
            }
            listview<MatchingStoryElementViewModel> {
                val ROW_HEIGHT = 24
                prefHeight = 2.0
                maxHeight = 2.0 + 11.5 * ROW_HEIGHT
                items.onChange {
                    prefHeight = (items.size * ROW_HEIGHT) + 2.0
                }
                cellFragment(scope, MentionSuggestion::class)
                state.mentionQueryState.onChange {
                    if (it is MentionQueryLoaded) {
                        items.setAll(it.prioritizedMatches)
                    }
                }
            }
        }
        content.add(vbox)
        setOnAutoHide {
            viewListener.cancelQuery()
        }
        state.mentionQueryState.onChange {
            when (it) {
                is TriggeredQuery -> if (!isShowing) {
                    textArea.getCharacterBoundsOnScreen(it.primedIndex, it.primedIndex + 1)
                        .ifPresent { bounds ->
                            x =
                                bounds.minX - 8.0 // TODO calculate the actual distance between the first character in the first item in the listview to the edge of the popup
                            y = bounds.maxY
                            show(this@ProseEditorView.currentWindow)
                        }
                }
                else -> if (isShowing) hide()
            }
        }
        isAutoHide = true
    }

    override val root: Parent = textArea.apply {
        addClass("prose-editor")
        isWrapText = true
        val onOutsideSelectionMousePressed = this.onOutsideSelectionMousePressed
        setOnOutsideSelectionMousePressed {
            viewListener.cancelQuery()
            onOutsideSelectionMousePressed.handle(it)
        }
    }

    init {
        properties["mentionMenu"] = mentionMenu
        state.content.onChange {
            textArea.replace(0, textArea.text.length, it, "")
        }
        state.mentions.onChange { list: ObservableList<ProseMention<*>>? ->
            if (list == null) return@onChange
            textArea.clearStyle(0, textArea.content.length)
            list.forEach {
                textArea.setStyle(
                    it.position.index,
                    it.position.index + it.position.length,
                    listOf(it.entityId.id.toString())
                )
            }
        }
        textArea.moveTo(0)
        textArea.requestFollowCaret()
        textArea.setOnKeyPressed {
            when (it.code) {
                KeyCode.ESCAPE -> {
                    viewListener.cancelQuery()
                }
                KeyCode.BACK_SPACE -> {
                    val queryState = state.mentionQueryState.value
                    if (queryState is TriggeredQuery) {
                        val nonEmptyQuery = NonBlankString.create(queryState.query.dropLast(1))
                        if (nonEmptyQuery != null) {
                            viewListener.getStoryElementsContaining(nonEmptyQuery)
                        } else {
                            viewListener.cancelQuery()
                        }
                    }
                }
            }
        }
        textArea.setOnKeyTyped {
            val queryState = state.mentionQueryState.value
            if (queryState is NoQuery) {
                if (it.character == "@") {
                    viewListener.primeMentionQuery(textArea.caretPosition)
                }
                return@setOnKeyTyped
            }
            val currentQuery = (queryState as? TriggeredQuery)?.query.orEmpty()
            if (it.character != KeyCode.BACK_SPACE.char) {
                val nonEmptyQuery = NonBlankString.create(currentQuery + it.character)
                if (nonEmptyQuery != null) {
                    viewListener.getStoryElementsContaining(nonEmptyQuery)
                }
            }

        }
        viewListener.getValidState()
    }

}