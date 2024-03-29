package com.soyle.stories.theme.characterConflict.components

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.cardHeader
import com.soyle.stories.common.existsWhen
import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.di.resolve
import com.soyle.stories.theme.characterConflict.*
import com.soyle.stories.theme.characterConflict.addDragAndDrop
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableStringValue
import javafx.collections.ListChangeListener
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.SnapshotParameters
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import tornadofx.*

class OpponentCard : ItemFragment<CharacterChangeOpponentViewModel?>() {

    private val model = resolve<CharacterConflictModel>()

    internal var onOpponentSelectedToBeMain: (String) -> Unit = {}
    internal var onRemoveOpponent: (String) -> Unit = {}
    private val opponentAttackProperty = ReadOnlyStringWrapper("")
    internal val opponentAttack: ReadOnlyStringProperty
        get() = opponentAttackProperty.readOnlyProperty
    private val opponentSimilaritiesProperty = ReadOnlyStringWrapper("")
    internal val opponentSimilarities: ReadOnlyStringProperty
        get() = opponentSimilaritiesProperty.readOnlyProperty
    private val opponentAbilityProperty = ReadOnlyStringWrapper("")
    internal val opponentAbility: ReadOnlyStringProperty
        get() = opponentAbilityProperty.readOnlyProperty


    private val isMainOpponent = SimpleBooleanProperty(false)
    private val isMinorOpponent = isMainOpponent.not()

    private val firstCard = SimpleBooleanProperty(false)
    private val isOnlyCard = SimpleBooleanProperty(false)
    private val lastCard = SimpleBooleanProperty(false)

    private val childrenFocusProperties = mutableListOf<BooleanExpression>()
    private val anyChildFocused = SimpleBooleanProperty(false)

    override val root: Parent = hbox {
        addClass(ComponentsStyles.card)
        existsWhen { itemProperty.isNotNull }
        isMainOpponent.onChange {
            if (it) disableDragAndDrop()
            else addDragAndDrop()
        }
        if (!isMainOpponent.value) addDragAndDrop()
    }

    init {
        with(root) {
            cardContent()
            reorderButtons()
        }
    }

    private fun Parent.cardContent(): Node {
        return vbox {
            opponentCardHeader()
            opponentPropertyFields()
        }
    }

    private fun Parent.opponentCardHeader() {
        cardHeader {
            label(itemProperty.select { it?.characterName.toProperty() })
            spacer()
            button("Main Opponent") {
                existsWhen { isMinorOpponent }
                childrenFocusProperties.add(focusedProperty())
                action {
                    val opponentId = itemProperty.value?.characterId ?: return@action
                    onOpponentSelectedToBeMain(opponentId)
                }
            }
            button("Remove Opponent") {
                childrenFocusProperties.add(focusedProperty())
                action {
                    val opponentId = itemProperty.value?.characterId ?: return@action
                    onRemoveOpponent(opponentId)
                }
            }
        }
    }

    private fun Parent.opponentPropertyFields(): Node {
        return hbox(spacing = 8.0) {
            addClass(ComponentsStyles.cardBody)
            addClass(ComponentsStyles.notFirstChild)
            isMainOpponent.onChange {
                if (it) style = ""
                else style { padding = box(0.px, 0.px, 16.px, 16.px) }
            }
            if (isMinorOpponent.value) style { padding = box(0.px, 0.px, 16.px, 16.px) }
            listOf(
                opponentPropertyField(
                    model.attackSectionLabel as ObservableStringValue,
                    CharacterChangeOpponentViewModel::attack,
                    opponentAttackProperty::set
                ),
                opponentPropertyField(
                    model.similaritiesSectionLabel as ObservableStringValue,
                    CharacterChangeOpponentViewModel::similarities,
                    opponentSimilaritiesProperty::set
                ),
                opponentPropertyField(
                    model.powerStatusOrAbilitiesLabel as ObservableStringValue,
                    CharacterChangeOpponentViewModel::powerStatusOrAbilities,
                    opponentAbilityProperty::set
                )
            ).forEach {
                it.hgrow = Priority.ALWAYS
            }
        }
    }

    fun Parent.opponentPropertyField(
        columnLabelProperty: ObservableStringValue,
        valueProperty: CharacterChangeOpponentViewModel.() -> String,
        onUpdate: (String) -> Unit
    ): Node =
        textarea(itemProperty.select { it?.valueProperty().toProperty() }) {
            prefRowCount = 3
            isWrapText = true
            onLoseFocus { onUpdate(text) }
            existsWhen {
                model.isLarge.or(model.selectedOpponentPropertyColumn.booleanBinding(columnLabelProperty) {
                    it == columnLabelProperty.value
                })
            }
        }

    private fun Parent.reorderButtons() {
        vbox(alignment = Pos.CENTER) {
            visibleWhen { isMinorOpponent.and(root.hoverProperty().or(anyChildFocused)) }
            managedWhen(isMinorOpponent)
            reorderUpButton()
            spacer()
            reorderDragHandle()
            spacer()
            reorderDownButton()
        }
    }

    private fun Parent.reorderUpButton() {
        button {
            visibleWhen(firstCard.not())
            graphic = MaterialIconView(MaterialIcon.SWAP_VERT)
            childrenFocusProperties.add(focusedProperty())
            action {
                val currentIndex = root.indexInParent
                if (currentIndex > 0) {
                    root.parent?.getChildList()?.swap(currentIndex, currentIndex - 1)
                }
            }
        }
    }

    private fun Parent.reorderDragHandle() {
        label {
            visibleWhen { isOnlyCard.not() }
            graphic = MaterialIconView(MaterialIcon.DRAG_HANDLE, "2em")
            style {
                cursor = Cursor.OPEN_HAND
            }
            isFocusTraversable = false
            setOnDragDetected {
                val board = root.startDragAndDrop(TransferMode.MOVE)
                board.setContent(ClipboardContent().apply {
                    put(opponentIdFormat, itemProperty.value?.characterId)
                })
                val snapshot = root.snapshot(SnapshotParameters(), null)

                board.setDragView(
                    snapshot,
                    snapshot.width - width + it.x,
                    (snapshot.height / 2)
                )
                it.consume()
            }
        }
    }

    private fun Parent.reorderDownButton() {
        button {
            visibleWhen(lastCard.not())
            graphic = MaterialIconView(MaterialIcon.SWAP_VERT)
            childrenFocusProperties.add(focusedProperty())
            action {
                val currentParent = root.parent ?: return@action
                val childList = currentParent.childrenUnmodifiable ?: return@action
                val currentIndex = childList.indexOf(root)
                if (currentIndex != -1 && currentIndex < childList.size - 1) {
                    currentParent.getChildList()?.swap(currentIndex, currentIndex + 1)
                }
            }
        }
    }

    private val mainOpponentListener =
        ChangeListener<Any?> { _, _, _ ->
            isMainOpponent.set(item == model.mainOpponent.value)
        }
    private var isListening: Boolean = false

    @Synchronized
    private fun stopListeningForMainOpponent() {
        if (!isListening) return
        isListening = false
        itemProperty.removeListener(mainOpponentListener)
        model.mainOpponent.removeListener(mainOpponentListener)
    }

    @Synchronized
    private fun addMainOpponentListenersIfNeeded() {
        if (isListening) return
        isListening = true
        itemProperty.addListener(mainOpponentListener)
        model.mainOpponent.addListener(mainOpponentListener)
        mainOpponentListener.changed(itemProperty, item, item)
    }

    init {
        anyChildFocused.bind(
            childrenFocusProperties.drop(1)
                .fold(childrenFocusProperties.first()) { prop, next ->
                    prop.or(next) as BooleanExpression
                })
        val childChangeListener = ListChangeListener<Node> {
            val list: List<Node> = root.parent?.childrenUnmodifiable ?: listOf()
            firstCard.value = list.firstOrNull() == root
            lastCard.value = list.lastOrNull() == root
            isOnlyCard.value = list.singleOrNull() == root
        }
        root.parentProperty().addListener { observable, oldValue, newValue ->
            oldValue?.childrenUnmodifiable?.removeListener(childChangeListener)
            newValue?.childrenUnmodifiable?.addListener(childChangeListener)
        }
        root.parent?.childrenUnmodifiable?.addListener(childChangeListener)
        itemProperty.onChange {
            if (it == null) stopListeningForMainOpponent()
            else addMainOpponentListenersIfNeeded()
        }
    }

}