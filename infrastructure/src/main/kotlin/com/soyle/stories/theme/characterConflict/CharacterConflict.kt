package com.soyle.stories.theme.characterConflict

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.common.components.*
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.resolve
import com.soyle.stories.soylestories.Styles
import de.jensd.fx.glyphs.emojione.EmojiOne
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.Observable
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.*
import javafx.collections.ListChangeListener
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Cursor.HAND
import javafx.scene.Cursor.cursor
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.SnapshotParameters
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DataFormat
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.util.Duration
import org.w3c.dom.events.EventTarget
import tornadofx.*
import tornadofx.ViewTransition.Direction
import tornadofx.ViewTransition.Slide

class CharacterConflict : View() {

    private val viewListener = resolve<CharacterConflictViewListener>()
    private val model = resolve<CharacterConflictModel>()

    private val isSmallProperty = SimpleBooleanProperty()
    private val isLargeProperty = isSmallProperty.not()

    override val root: Form = form {
        fieldset(labelPosition = Orientation.VERTICAL) {
            vbox(spacing = 8.0) {
                responsiveBox(isSmallProperty, hSpacing = 8.0, vSpacing = 8.0) {
                    centralConflictField()
                    perspectiveCharacterSelection()
                }
                val tabSelection = sectionTabs()
                scrollpane {
                    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                    vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
                    isFitToWidth = true
                    addClass("edge-to-edge")
                    content = vbox {
                        characterChangeSectionTitle()
                        characterChangeFields(tabSelection)
                        hbox {
                            opponentsSectionTitle()
                            spacer()
                            addOpponentSelection(tabSelection)
                        }
                        vbox(spacing = 0.0) {
                            isFillWidth = true
                            visibleWhen {
                                model.selectedPerspectiveCharacter.isNotNull.and(
                                    isLargeProperty.or(
                                        tabSelection.isEqualTo(
                                            model.opponentSectionsLabel as SimpleStringProperty
                                        )
                                    )
                                )
                            }
                            managedProperty().bind(visibleProperty())
                            val selectedColumn = opponentFieldLabels()
                            opponents(selectedColumn)
                        }
                    }
                }
            }
        }
    }

    private fun Parent.opponents(
        selectedColumn: ReadOnlyStringProperty
    ) {
        val attackColumnSelected =
            selectedColumn.isEqualTo(model.attackSectionLabel as SimpleStringProperty)
        val similaritiesColumnSelected =
            selectedColumn.isEqualTo(model.similaritiesSectionLabel as SimpleStringProperty)
        val powerStatusOrAbilitiesColumnSelected =
            selectedColumn.isEqualTo(model.powerStatusOrAbilitiesLabel as SimpleStringProperty)
        vbox {
            label("Main Opponent") {
                style {
                    fontSize = 1.25.em
                }
            }
            hbox {
                associateChildrenTo(model.mainOpponent.select {
                    mapOf(it.characterId to it).toProperty()
                }) { opponentModel ->
                    card {
                        val card = this
                        val anyChildrenFocused = SimpleBooleanProperty(false)
                        val childrenFocusProperties = mutableListOf<BooleanExpression>()
                        hbox {
                            vbox {
                                cardHeader {
                                    label(opponentModel.select { it?.characterName.toProperty() })
                                }
                                hbox(spacing = 8.0) {
                                    addClass(ComponentsStyles.cardBody)
                                    addClass(ComponentsStyles.notFirstChild)
                                    textarea(opponentModel.select { it?.attack.toProperty() }) {
                                        prefRowCount = 3
                                        isWrapText = true
                                        visibleWhen { isLargeProperty.or(attackColumnSelected) }
                                        managedProperty().bind(visibleProperty())
                                        hgrow = Priority.ALWAYS
                                        childrenFocusProperties.add(focusedProperty())
                                    }
                                    textarea(opponentModel.select { it?.similarities.toProperty() }) {
                                        prefRowCount = 3
                                        isWrapText = true
                                        visibleWhen { isLargeProperty.or(similaritiesColumnSelected) }
                                        managedProperty().bind(visibleProperty())
                                        hgrow = Priority.ALWAYS
                                        childrenFocusProperties.add(focusedProperty())
                                    }
                                    textarea(opponentModel.select { it?.powerStatusOrAbilities.toProperty() }) {
                                        prefRowCount = 3
                                        isWrapText = true
                                        visibleWhen {
                                            isLargeProperty.or(
                                                powerStatusOrAbilitiesColumnSelected
                                            )
                                        }
                                        managedProperty().bind(visibleProperty())
                                        hgrow = Priority.ALWAYS
                                        childrenFocusProperties.add(focusedProperty())
                                    }
                                }
                            }
                        }
                        anyChildrenFocused.bind(
                            childrenFocusProperties.drop(1)
                                .fold(childrenFocusProperties.first()) { prop, next ->
                                    prop.or(next) as BooleanExpression
                                })
                    }
                }
            }
            label("Minor Opponents") {
                style {
                    fontSize = 1.25.em
                }
            }
        }
        vbox(spacing = 8.0) {
            val cardHolder = this
            associateChildrenTo(model.opponents.select {
                it.associateBy { it.characterId }.toProperty()
            }) { opponentModel ->
                card {
                    val card = this
                    val anyChildrenFocused = SimpleBooleanProperty(false)
                    val childrenFocusProperties = mutableListOf<BooleanExpression>()
                    addDragAndDrop()
                    hbox {
                        vbox {
                            cardHeader {
                                label(opponentModel.select { it?.characterName.toProperty() })
                                spacer()
                                button("Main Opponent") {
                                    visibleWhen(this@card.hoverProperty().or(anyChildrenFocused))
                                    childrenFocusProperties.add(focusedProperty())
                                    action {
                                        val perspectiveCharacterId = model.selectedPerspectiveCharacter.value?.characterId ?: return@action
                                        val opponentId = opponentModel.value?.characterId ?: return@action
                                        viewListener.makeOpponentMainOpponent(perspectiveCharacterId, opponentId)
                                    }
                                }
                            }
                            hbox(spacing = 8.0) {
                                addClass(ComponentsStyles.cardBody)
                                addClass(ComponentsStyles.notFirstChild)
                                style {
                                    padding = box(0.px, 0.px, 16.px, 16.px)
                                }
                                textarea(opponentModel.select { it?.attack.toProperty() }) {
                                    prefRowCount = 3
                                    isWrapText = true
                                    visibleWhen { isLargeProperty.or(attackColumnSelected) }
                                    managedProperty().bind(visibleProperty())
                                    hgrow = Priority.ALWAYS
                                    childrenFocusProperties.add(focusedProperty())
                                }
                                textarea(opponentModel.select { it?.similarities.toProperty() }) {
                                    prefRowCount = 3
                                    isWrapText = true
                                    visibleWhen { isLargeProperty.or(similaritiesColumnSelected) }
                                    managedProperty().bind(visibleProperty())
                                    hgrow = Priority.ALWAYS
                                    childrenFocusProperties.add(focusedProperty())
                                }
                                textarea(opponentModel.select { it?.powerStatusOrAbilities.toProperty() }) {
                                    prefRowCount = 3
                                    isWrapText = true
                                    visibleWhen {
                                        isLargeProperty.or(
                                            powerStatusOrAbilitiesColumnSelected
                                        )
                                    }
                                    managedProperty().bind(visibleProperty())
                                    hgrow = Priority.ALWAYS
                                    childrenFocusProperties.add(focusedProperty())
                                }
                            }
                        }
                        vbox(alignment = Pos.CENTER) {
                            visibleWhen(this@card.hoverProperty().or(anyChildrenFocused))
                            button {
                                val listener = ListChangeListener<Node> {
                                    isVisible = card.indexInParent > 0
                                }
                                cardHolder.children.addListener(listener)
                                opponentModel.onChangeUntil({ it == null }) {
                                    if (it == null) cardHolder.children.removeListener(listener)
                                }
                                graphic = MaterialIconView(MaterialIcon.SWAP_VERT)
                                childrenFocusProperties.add(focusedProperty())
                                isVisible = card.indexInParent > 0
                                action {
                                    val currentIndex = card.indexInParent
                                    if (currentIndex > 0) {
                                        cardHolder.children.swap(currentIndex, currentIndex - 1)
                                    }
                                }
                            }
                            spacer()
                            label {
                                val listener = ListChangeListener<Node> {
                                    isVisible = it.list.size > 1
                                }
                                cardHolder.children.addListener(listener)
                                opponentModel.onChangeUntil({ it == null }) {
                                    if (it == null) cardHolder.children.removeListener(listener)
                                }
                                isVisible = cardHolder.children.size > 1
                                graphic = MaterialIconView(MaterialIcon.DRAG_HANDLE, "2em")
                                style {
                                    cursor = Cursor.OPEN_HAND
                                }
                                isFocusTraversable = false
                                setOnDragDetected {
                                    val board = this@card.startDragAndDrop(TransferMode.MOVE)
                                    board.setContent(ClipboardContent().apply {
                                        put(opponentIdFormat, opponentModel.value?.characterId)
                                    })
                                    val snapshot =
                                        this@card.snapshot(SnapshotParameters(), null)

                                    board.setDragView(
                                        snapshot,
                                        snapshot.width - width + it.x,
                                        (snapshot.height / 2)
                                    )
                                    it.consume()
                                }
                            }
                            spacer()
                            button {
                                graphic = MaterialIconView(MaterialIcon.SWAP_VERT)
                                childrenFocusProperties.add(focusedProperty())
                                val listener = ListChangeListener<Node> {
                                    isVisible = card != cardHolder.children.lastOrNull()
                                }
                                cardHolder.children.addListener(listener)
                                opponentModel.onChangeUntil({ it == null }) {
                                    if (it == null) cardHolder.children.removeListener(listener)
                                }
                                isVisible = card != cardHolder.children.last()
                                action {
                                    val currentIndex = card.indexInParent
                                    if (currentIndex < cardHolder.children.size - 1) {
                                        cardHolder.children.swap(currentIndex, currentIndex + 1)
                                    }
                                }
                            }
                        }
                    }
                    anyChildrenFocused.bind(
                        childrenFocusProperties.drop(1)
                            .fold(childrenFocusProperties.first()) { prop, next ->
                                prop.or(next) as BooleanExpression
                            })
                }
            }
        }
    }

    private fun Parent.opponentFieldLabels(): ReadOnlyStringProperty {
        val opponentPropertyLabels = listOf(
            model.attackSectionLabel,
            model.similaritiesSectionLabel,
            model.powerStatusOrAbilitiesLabel
        )
        val selectedColumn = SimpleStringProperty()
        selectedColumn.cleanBind(model.attackSectionLabel)
        hbox(spacing = 8.0, alignment = Pos.BOTTOM_CENTER) {
            paddingHorizontal = 32.0
            visibleWhen(isLargeProperty)
            managedProperty().bind(visibleProperty())
            opponentPropertyLabels.forEach {
                hbox(alignment = Pos.BOTTOM_CENTER) {
                    hgrow = Priority.ALWAYS
                    prefWidth = 0.0
                    minWidth = 0.0
                    label(it) {
                        style {
                            fontWeight = FontWeight.BOLD
                        }
                    }
                }
            }
        }
        menubutton {
            visibleWhen(isSmallProperty)
            managedProperty().bind(visibleProperty())
            textProperty().bind(selectedColumn)
            opponentPropertyLabels.forEach {
                item("") {
                    textProperty().bind(it)
                    action { selectedColumn.cleanBind(it) }
                }
            }
        }
        return selectedColumn
    }

    private fun Parent.opponentsSectionTitle() {
        label(model.opponentSectionsLabel) {
            visibleWhen(isLargeProperty)
            managedProperty().bind(visibleProperty())
            style {
                fontSize = 1.25.em
            }
        }
    }
    private fun Parent.addOpponentSelection(tabSelection: ReadOnlyStringProperty) {
        menubutton("Add Opponent") {
            visibleWhen {
                model.selectedPerspectiveCharacter.isNotNull.and(
                    isLargeProperty.or(
                        tabSelection.isEqualTo(
                            model.opponentSectionsLabel as SimpleStringProperty
                        )
                    )
                )
            }
            managedProperty().bind(visibleProperty())
            val loadingItem = item("Loading...") {
                isDisable = true
            }
            model.availableOpponents.onChange {
                items.clear()
                when {
                    it == null -> items.add(loadingItem)
                    it.isEmpty() -> {
                        item("No available characters") { isDisable = true }
                        item("Create New Character") {
                            action {
                                val perspectiveCharacterId =
                                    model.selectedPerspectiveCharacter.value?.characterId
                                        ?: return@action
                                createCharacterDialog(
                                    model.scope.projectScope,
                                    model.scope.themeId,
                                    useAsOpponentForCharacter = perspectiveCharacterId
                                )
                            }
                        }
                    }
                    else -> {
                        item("Characters in Theme") {
                            addClass(ComponentsStyles.contextMenuSectionHeaderItem)
                            isDisable = true
                        }
                        it.filter { it.isInTheme }.forEach {
                            item(it.characterName) {
                                addClass(ComponentsStyles.contextMenuSectionedItem)
                                action {
                                    val perspectiveCharacter =
                                        model.selectedPerspectiveCharacter.value
                                            ?: return@action
                                    viewListener.addOpponent(
                                        perspectiveCharacter.characterId,
                                        it.characterId
                                    )
                                }
                            }
                        }
                        item("Other Characters in Story") {
                            addClass(ComponentsStyles.contextMenuSectionHeaderItem)
                            isDisable = true
                        }
                        item("Create New Character") {
                            addClass(ComponentsStyles.contextMenuSectionedItem)
                            action {
                                val perspectiveCharacterId =
                                    model.selectedPerspectiveCharacter.value?.characterId
                                        ?: return@action
                                createCharacterDialog(
                                    model.scope.projectScope,
                                    model.scope.themeId,
                                    useAsOpponentForCharacter = perspectiveCharacterId
                                )
                            }
                        }
                        it.filterNot { it.isInTheme }.forEach {
                            customitem {
                                addClass(ComponentsStyles.contextMenuSectionedItem)
                                addClass(ComponentsStyles.discouragedSelection)
                                content = label(it.characterName) {
                                    tooltip {
                                        showDelay = Duration.seconds(0.0)
                                        hideDelay = Duration.seconds(0.0)
                                        style { fontSize = 1.em }
                                        text =
                                            "${it.characterName} is not included in this theme.  By " +
                                                    "selecting them, they will be included as a Minor Character."
                                    }
                                }
                                action {
                                    val perspectiveCharacter =
                                        model.selectedPerspectiveCharacter.value
                                            ?: return@action
                                    viewListener.addOpponent(
                                        perspectiveCharacter.characterId,
                                        it.characterId
                                    )
                                }
                            }
                        }
                    }
                }
            }

            setOnShowing {
                val perspectiveCharacter =
                    model.selectedPerspectiveCharacter.value ?: return@setOnShowing
                viewListener.getAvailableOpponents(perspectiveCharacter.characterId)
            }
            setOnHidden {
                model.availableOpponents.value = null
            }
        }
    }

    private fun Parent.characterChangeFields(tabSelection: ReadOnlyStringProperty): Fieldset {
        return fieldset(labelPosition = Orientation.VERTICAL) {
            responsiveBox(isSmallProperty, hSpacing = 8.0, vSpacing = 8.0) {
                visibleWhen {
                    model.selectedPerspectiveCharacter.isNotNull.and(
                        isLargeProperty.or(
                            tabSelection.isEqualTo(
                                model.characterChangeLabel as SimpleStringProperty
                            )
                        )
                    )
                }
                managedProperty().bind(visibleProperty())
                listOf(
                    model.desireLabel to model.desire,
                    model.psychologicalWeaknessLabel to model.psychologicalWeakness,
                    model.moralWeaknessLabel to model.moralWeakness,
                    model.characterChangeLabel to model.characterChange
                ).forEach { (labelProperty, valueProperty) ->
                    field {
                        textProperty.bind(labelProperty)
                        hgrow = Priority.ALWAYS
                        textfield(valueProperty) {
                            hgrow = Priority.ALWAYS
                        }
                    }
                }
            }
        }
    }

    private fun Parent.characterChangeSectionTitle(): Label {
        return label(model.characterChangeLabel) {
            visibleWhen(isLargeProperty)
            managedProperty().bind(visibleProperty())
            style {
                fontSize = 1.25.em
            }
        }
    }

    private fun Parent.sectionTabs(): ReadOnlyStringProperty {
        val tabSelection = SimpleStringProperty()
        togglegroup {
            hbox {
                visibleWhen { isSmallProperty }
                managedProperty().bind(visibleProperty())
                listOf(
                    model.characterChangeLabel,
                    model.opponentSectionsLabel
                ).forEach {
                    togglebutton(it, group = this@togglegroup) {
                        hgrow = Priority.ALWAYS
                        maxWidth = Double.MAX_VALUE
                        action {
                            if (!isSelected) isSelected = true
                            tabSelection.cleanBind(it)
                        }
                    }
                }
            }
        }
        tabSelection.cleanBind(model.characterChangeLabel)
        return tabSelection
    }

    private fun Parent.centralConflictField() {
        field {
            textProperty.bind(model.centralConflictFieldLabel)
            hgrow = Priority.ALWAYS
            textfield(model.centralConflict) {
                hgrow = Priority.ALWAYS
            }
        }
    }

    private fun Parent.perspectiveCharacterSelection() {
        field {
            textProperty.bind(model.perspectiveCharacterLabel)
            hgrow = Priority.NEVER
            usePrefWidth = true
            menubutton {
                textProperty().bind(model.selectedPerspectiveCharacter.select { it?.characterName.toProperty() })
                hgrow = Priority.ALWAYS
                maxWidth = Double.MAX_VALUE
                val loadingItem = item("Loading...") {
                    isDisable = true
                }
                val createCharacterItem = MenuItem("[Create New Character]").apply {
                    action {
                        createCharacterDialog(
                            model.scope.projectScope,
                            model.scope.themeId,
                            true
                        )
                    }
                }
                model.availablePerspectiveCharacters.onChange {
                    items.clear()
                    when {
                        it == null -> items.add(loadingItem)
                        it.isEmpty() -> {
                            items.add(createCharacterItem.apply {
                                if (hasClass(ComponentsStyles.contextMenuSectionedItem))
                                    removeClass(ComponentsStyles.contextMenuSectionedItem)
                            })
                            item("No available characters") { isDisable = true }
                        }
                        else -> {
                            item("Major Characters") {
                                addClass(ComponentsStyles.contextMenuSectionHeaderItem)
                                isDisable = true
                            }
                            it.filter { it.isMajorCharacter }.forEach {
                                item(it.characterName) {
                                    addClass(ComponentsStyles.contextMenuSectionedItem)
                                    action {
                                        model.selectedPerspectiveCharacter.value =
                                            CharacterItemViewModel(it.characterId, it.characterName, "")
                                        viewListener.getValidState(it.characterId)
                                    }
                                }
                            }
                            item("Minor Characters") {
                                addClass(ComponentsStyles.contextMenuSectionHeaderItem)
                                isDisable = true
                            }
                            it.filterNot { it.isMajorCharacter }.forEach {
                                customitem {
                                    addClass(ComponentsStyles.contextMenuSectionedItem)
                                    addClass(ComponentsStyles.discouragedSelection)
                                    content = label(it.characterName) {
                                        tooltip {
                                            showDelay = Duration.seconds(0.0)
                                            hideDelay = Duration.seconds(0.0)
                                            style { fontSize = 1.em }
                                            text =
                                                "${it.characterName} is a minor character in this theme." +
                                                        "  By selecting this character, they will be promoted" +
                                                        " to a major character in the theme.  This means they" +
                                                        " will gain a character arc."
                                        }
                                    }
                                    action {
                                        model.selectedPerspectiveCharacter.value =
                                            CharacterItemViewModel(it.characterId, it.characterName, "")
                                        viewListener.getValidState(it.characterId)
                                    }
                                }
                            }
                            item("Remaining Characters in Story") {
                                addClass(ComponentsStyles.contextMenuSectionHeaderItem)
                                isDisable = true
                            }
                            items.add(createCharacterItem.apply {
                                if (!hasClass(ComponentsStyles.contextMenuSectionedItem))
                                    addClass(ComponentsStyles.contextMenuSectionedItem)
                            })
                        }
                    }
                }
                setOnShowing {
                    viewListener.getAvailableCharacters()
                }
                setOnHidden {
                    model.availablePerspectiveCharacters.value = null
                }
            }
        }
    }

    init {
        model.invalidatedProperty().onChange {
            getValidStateIfInvalid(it, null)
        }
        root.widthProperty().onChange {
            if (it < 600) isSmallProperty.set(true)
            else isSmallProperty.set(false)
        }
        getValidStateIfInvalid(model.invalidated, model.scope.type.characterId?.toString())
    }

    private fun getValidStateIfInvalid(invalidated: Boolean, characterId: String?) {
        if (invalidated) viewListener.getValidState(characterId)
    }

    companion object {
        private val opponentIdFormat = DataFormat("opponent-id")
    }

}