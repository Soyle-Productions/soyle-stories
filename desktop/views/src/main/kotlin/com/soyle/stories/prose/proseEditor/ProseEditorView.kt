package com.soyle.stories.prose.proseEditor

import com.soyle.stories.character.create.createCharacter
import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.prose.*
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import javafx.collections.ObservableList
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.collections.set

class ProseEditorView : Fragment() {

    override val scope: ProseEditorScope = super.scope as ProseEditorScope

    private val viewListener = resolve<ProseEditorViewListener>()
    private val state = resolve<ProseEditorState>()

    private val textArea = ProseEditorTextArea()
    private val mentionMenu = MatchingStoryElementsPopup(scope, state.mentionQueryState)
    private val mentionIssueMenu = MentionIssueMenu(this)
    private fun mentionIssueItems(hitIndex: Int, mention: Mention) = listOf(
        clearMentionOption(mention),
        clearAllMentionOption(mention),
        removeMentionOption(mention),
        removeAllMentionsOption(mention)
    ) + replaceMentionOptions(hitIndex, mention)

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
            cancelActiveQueryWhenClickedOutsideSelection()
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
                val segment = textArea.getSegmentContaining(characterIndex)
                if (segment is Mention && segment.issue != null) {
                    mentionIssueMenu.properties["relative-mention"] = segment
                    mentionIssueMenu.items.setAll(mentionIssueItems(characterIndex, segment))
                    it.consume()
                    if (it.isKeyboardTrigger) {
                        val screenPosition =
                            getCharacterBoundsOnScreen(characterIndex, characterIndex + 1).get()
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

    private fun ProseEditorTextArea.cancelActiveQueryWhenClickedOutsideSelection() {
        val onOutsideSelectionMousePressed = this.onOutsideSelectionMousePressed
        setOnOutsideSelectionMousePressed {
            viewListener.cancelQuery()
            onOutsideSelectionMousePressed.handle(it)
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
            textArea.undoManager.forgetHistory()
            textArea.clear()
            elements?.forEach {
                when (it) {
                    is BasicText -> textArea.appendText(it.text)
                    is Mention -> textArea.append(it, listOf())
                }
            }
            textArea.moveTo(currentPosition.coerceAtMost(textArea.text.length).coerceAtLeast(0))
            pushingUpdate = false
        }
        textArea.richChanges().feedTo {
            if (pushingUpdate) return@feedTo
            updateCausedByInput = true
            state.content.setAll(textArea.paragraphs.flatMap { it.segments + BasicText("\n") }.dropLast(1))
            updateCausedByInput = false
        }
        textArea.moveTo(0)
        textArea.requestFollowCaret()

        viewListener.getValidState()
    }

    private fun clearMentionOption(mention: Mention): MenuItem {
        return MenuItem("Clear this Mention of ${mention.text} and Use Normal Text").apply {
            id = "clear-mention"
            action {
                textArea.clearMention(mention)
                viewListener.save()
            }
        }
    }

    private fun clearAllMentionOption(mention: Mention): MenuItem {
        return MenuItem("Clear all Mentions of ${mention.text} and Use Normal Text").apply {
            id = "clear-mentions"
            action {
                textArea.clearAllMentionsOfEntity(mention)
                viewListener.save()
            }
        }
    }

    private fun removeMentionOption(mention: Mention): MenuItem {
        return MenuItem("Remove this Mention of ${mention.text} and Remove the Text").apply {
            id = "remove-mention"
            action {
                textArea.removeMention(mention)
                viewListener.save()
            }
        }
    }

    private fun removeAllMentionsOption(mention: Mention): MenuItem {
        return MenuItem("Remove all Mentions of ${mention.text} and Remove the Text").apply {
            id = "remove-mentions"
            action {
                textArea.removeAllMentionsOfEntity(mention)
                viewListener.save()
            }
        }
    }

    private fun replaceMentionOptions(hitIndex: Int, mention: Mention): List<MenuItem> {
        state.replacementOptions.clear()
        val items = listOf(
            replaceMentionOption(hitIndex, mention),
            replaceAllMentionsOption(mention)
        )
        viewListener.getMentionReplacementOptions(mention)
        return items
    }

    private fun replaceMentionOption(hitIndex: Int, mention: Mention): MenuItem {
        return Menu("Replace this Mention of ${mention.text} with...").apply {
            id = "replace-mention"
            state.replacementOptions.onChangeOnce {
                if (it == null) return@onChangeOnce
                val creationOption = getCreationReplacementOption(
                    mention.entityId,
                    onNewMentionedEntity = {
                        val oldMention =
                            textArea.getSegmentContaining(hitIndex) as? Mention ?: return@getCreationReplacementOption
                        textArea.replaceMention(oldMention, with = it)
                        viewListener.save()
                    }
                )
                items.setAll(
                    *(listOf(creationOption) + state.replacementOptions.map {
                        MenuItem(it.name).apply {
                            action {
                                textArea.replaceMention(mention, Mention(it.name, it.entityId))
                                viewListener.save()
                            }
                        }
                    }).toTypedArray()
                )
            }
            item("Loading Replacement Options...") {
                graphic = progressindicator()
            }
        }
    }

    private fun replaceAllMentionsOption(mention: Mention): MenuItem {
        return Menu("Replace all Mentions of ${mention.text} with...").apply {
            id = "replace-mentions"
            state.replacementOptions.onChangeOnce {
                if (it == null) return@onChangeOnce
                val creationOption = getCreationReplacementOption(
                    mention.entityId,
                    onNewMentionedEntity = {
                        textArea.replaceAllMentionsOfEntity(mention, with = it)
                        viewListener.save()
                    }
                )
                items.setAll(
                    *(listOf(creationOption) + state.replacementOptions.map {
                        MenuItem(it.name).apply {
                            action {
                                textArea.replaceAllMentionsOfEntity(mention, with = Mention(it.name, it.entityId))
                                viewListener.save()
                            }
                        }
                    }).toTypedArray()
                )
            }
            item("Loading Replacement Options...") {
                graphic = progressindicator()
            }
        }
    }

    private fun getCreationReplacementOption(
        entityId: MentionedEntityId<*>,
        onNewMentionedEntity: (Mention) -> Unit
    ): MenuItem {
        return when (entityId) {
            is MentionedCharacterId -> MenuItem("Create New Character").apply {
                action {
                    createCharacter(scope.projectScope)
                }
            }
            is MentionedLocationId -> MenuItem("Create New Location").apply {
                action {
                    scope.projectScope.get<CreateLocationDialog.Factory>().invoke {
                        onNewMentionedEntity(Mention(it.locationName, Location.Id(it.locationId).mentioned()))
                    }.show(currentWindow)
                }
            }
            is MentionedSymbolId -> MenuItem("Create New Symbol").apply {
                action {
                    CreateSymbolDialog(
                        scope.projectScope,
                        entityId.themeId.uuid.toString()
                    ) { createdSymbol ->
                        onNewMentionedEntity(
                            Mention(
                                createdSymbol.symbolName,
                                Symbol.Id(createdSymbol.symbolId).mentioned(Theme.Id(createdSymbol.themeId))
                            )
                        )
                    }
                }
            }
        }
    }

    class Styles : Stylesheet() {
        companion object {

            val proseEditorTextArea by cssclass()
            val mention by cssclass()
            val problem by cssclass()
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
            mention {
                val transparentHighlight = Color.rgb(
                    (com.soyle.stories.soylestories.Styles.Blue.red * 255).toInt(),
                    (com.soyle.stories.soylestories.Styles.Blue.green * 255).toInt(),
                    (com.soyle.stories.soylestories.Styles.Blue.blue * 255).toInt(),
                    0.2,
                )
                unsafe("-rtfx-background-color", raw(transparentHighlight.css))
                fill = com.soyle.stories.soylestories.Styles.Blue
            }
            problem {
                val transparentHighlight = Color.rgb(
                    (com.soyle.stories.soylestories.Styles.Orange.red * 255).toInt(),
                    (com.soyle.stories.soylestories.Styles.Orange.green * 255).toInt(),
                    (com.soyle.stories.soylestories.Styles.Orange.blue * 255).toInt(),
                    0.2,
                )
                unsafe("-rtfx-background-color", raw(transparentHighlight.css))
                fill = com.soyle.stories.soylestories.Styles.Orange
            }
        }
    }

}
