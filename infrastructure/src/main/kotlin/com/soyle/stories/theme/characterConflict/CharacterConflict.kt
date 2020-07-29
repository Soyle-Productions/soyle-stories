package com.soyle.stories.theme.characterConflict

import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialog
import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.common.components.*
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.MenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.util.Duration
import tornadofx.*

class CharacterConflict : View() {

    private val viewListener = resolve<CharacterConflictViewListener>()
    private val model = resolve<CharacterConflictModel>()

    val isSmallProperty = SimpleBooleanProperty()
    val isLargeProperty = isSmallProperty.not()

    override val root: Form = form {
        widthProperty().onChange {
            if (it < 600) isSmallProperty.set(true)
            else isSmallProperty.set(false)
        }
        fieldset(labelPosition = Orientation.VERTICAL) {
            vbox(spacing = 8.0) {
                responsiveBox(isSmallProperty, hSpacing = 8.0, vSpacing = 8.0) {
                    field {
                        textProperty.bind(model.centralConflictFieldLabel)
                        hgrow = Priority.ALWAYS
                        textfield(model.centralConflict) {
                            hgrow = Priority.ALWAYS
                        }
                    }
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
                            }/*
                            val createCharacterItem = MenuItem("[Create New Character]").apply {
                                action {
                                    createCharacterDialog(scope.projectScope, scope.type.themeId.toString())
                                }
                            }*/
                            model.availablePerspectiveCharacters.onChange {
                                items.clear()
                                when {
                                    it == null -> items.add(loadingItem)
                                    it.isEmpty() -> {
                                        //items.add(createCharacterItem)
                                        item("No available characters") { isDisable = true }
                                    }
                                    else -> {
                                        //items.add(createCharacterItem)
                                        it.forEach {
                                            item(it.characterName) {
                                                action {
                                                    model.selectedPerspectiveCharacter.value = it
                                                    viewListener.getValidState(it.characterId)
                                                }
                                            }
                                        }
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
                val tabSelection = SimpleStringProperty("")
                togglegroup {
                    hbox {
                        visibleWhen { isSmallProperty }
                        managedProperty().bind(visibleProperty())
                        togglebutton(model.characterChangeLabel, group = this@togglegroup) {
                            hgrow = Priority.ALWAYS
                            maxWidth = Double.MAX_VALUE
                            action {
                                if (! isSelected) isSelected = true
                                tabSelection.cleanBind(model.characterChangeLabel)
                            }
                        }
                        togglebutton(model.opponentSectionsLabel, group = this@togglegroup) {
                            hgrow = Priority.ALWAYS
                            maxWidth = Double.MAX_VALUE
                            action {
                                if (! isSelected) isSelected = true
                                tabSelection.cleanBind(model.opponentSectionsLabel)
                            }
                        }
                    }
                }
                label(model.characterChangeLabel) {
                    visibleWhen(isLargeProperty)
                    managedProperty().bind(visibleProperty())
                    style {
                        fontSize = 1.25.em
                    }
                }
                responsiveBox(isSmallProperty, hSpacing = 8.0, vSpacing = 8.0) {
                    visibleWhen { model.selectedPerspectiveCharacter.isNotNull.and(isLargeProperty.or(tabSelection.isEqualTo(model.characterChangeLabel as SimpleStringProperty))) }
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
                vbox {
                    isFillWidth = true
                    visibleWhen { model.selectedPerspectiveCharacter.isNotNull.and(isLargeProperty.or(tabSelection.isEqualTo(model.opponentSectionsLabel as SimpleStringProperty))) }
                    managedProperty().bind(visibleProperty())
                    val selectedColumn = SimpleStringProperty("")
                    selectedColumn.cleanBind(model.attackSectionLabel)
                    val attackColumnSelected = selectedColumn.isEqualTo(model.attackSectionLabel as SimpleStringProperty)
                    val similaritiesColumnSelected = selectedColumn.isEqualTo(model.similaritiesSectionLabel as SimpleStringProperty)
                    val powerStatusOrAbilitiesColumnSelected = selectedColumn.isEqualTo(model.powerStatusOrAbilitiesLabel as SimpleStringProperty)
                    hbox {
                        label(model.opponentSectionsLabel) {
                            visibleWhen(isLargeProperty)
                            managedProperty().bind(visibleProperty())
                            style {
                                fontSize = 1.25.em
                            }
                        }
                        spacer()
                        menubutton("Add Opponent") {
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
                                                val perspectiveCharacterId = model.selectedPerspectiveCharacter.value?.characterId
                                                    ?: return@action
                                                createCharacterDialog(
                                                    model.scope.projectScope,
                                                    model.scope.themeId,
                                                    perspectiveCharacterId
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
                                                    val perspectiveCharacter = model.selectedPerspectiveCharacter.value ?: return@action
                                                    viewListener.addOpponent(perspectiveCharacter.characterId, it.characterId)
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
                                                val perspectiveCharacterId = model.selectedPerspectiveCharacter.value?.characterId
                                                    ?: return@action
                                                createCharacterDialog(
                                                    model.scope.projectScope,
                                                    model.scope.themeId,
                                                    perspectiveCharacterId
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
                                                        text = "${it.characterName} is not included in this theme.  By " +
                                                                "selecting them, they will be included as a Minor Character."
                                                    }
                                                }
                                                action {
                                                    val perspectiveCharacter = model.selectedPerspectiveCharacter.value ?: return@action
                                                    viewListener.addOpponent(perspectiveCharacter.characterId, it.characterId)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            setOnShowing {
                                val perspectiveCharacter = model.selectedPerspectiveCharacter.value ?: return@setOnShowing
                                viewListener.getAvailableOpponents(perspectiveCharacter.characterId)
                            }
                            setOnHidden {
                                model.availableOpponents.value = null
                            }
                        }
                    }
                    val opponentPropertyLabels = listOf(
                        model.attackSectionLabel,
                        model.similaritiesSectionLabel,
                        model.powerStatusOrAbilitiesLabel
                    )
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
                                    style { fontWeight = FontWeight.BOLD }
                                }
                            }
                        }
                    }
                    menubutton {
                        visibleWhen(isSmallProperty)
                        textProperty().bind(selectedColumn)
                        opponentPropertyLabels.forEach {
                            item("") {
                                textProperty().bind(it)
                                action { selectedColumn.cleanBind(it) }
                            }
                        }
                    }
                    vbox(spacing = 8.0) {
                        associateChildrenTo(model.opponents.select {
                            it.associateBy { it.characterId }.toProperty()
                        }) { opponentModel ->
                            card {
                                cardHeader {
                                    label(opponentModel.select { it?.characterName.toProperty() })
                                }
                                hbox(spacing = 8.0) {
                                    addClass(ComponentsStyles.cardBody)
                                    addClass(ComponentsStyles.notFirstChild)
                                    textarea(opponentModel.select { it?.attack.toProperty() }) {
                                        prefRowCount = 3
                                        visibleWhen { isLargeProperty.or(attackColumnSelected) }
                                        managedProperty().bind(visibleProperty())
                                        hgrow = Priority.ALWAYS
                                    }
                                    textarea(opponentModel.select { it?.similarities.toProperty() }) {
                                        prefRowCount = 3
                                        visibleWhen { isLargeProperty.or(similaritiesColumnSelected) }
                                        managedProperty().bind(visibleProperty())
                                        hgrow = Priority.ALWAYS
                                    }
                                    textarea(opponentModel.select { it?.powerStatusOrAbilities.toProperty() }) {
                                        prefRowCount = 3
                                        visibleWhen { isLargeProperty.or(powerStatusOrAbilitiesColumnSelected) }
                                        managedProperty().bind(visibleProperty())
                                        hgrow = Priority.ALWAYS
                                    }
                                    opponentModel.onChangeUntil({ it == null }) {
                                        if (it == null) removeFromParent()
                                    }
                                }
                            }
                        }
                    }
                }
                tabSelection.cleanBind(model.characterChangeLabel)
            }
        }
    }

    init {
        model.invalidatedProperty().onChange {
            getValidStateIfInvalid(it, null)
        }
        getValidStateIfInvalid(model.invalidated, model.scope.type.characterId?.toString())
    }

    private fun getValidStateIfInvalid(invalidated: Boolean, characterId: String?) {
        if (invalidated) viewListener.getValidState(characterId)
    }

}