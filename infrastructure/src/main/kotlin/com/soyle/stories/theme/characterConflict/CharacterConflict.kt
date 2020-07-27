package com.soyle.stories.theme.characterConflict

import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.common.components.PopOutEditBox
import com.soyle.stories.common.components.editableText
import com.soyle.stories.common.components.popOutEditBox
import com.soyle.stories.common.components.responsiveBox
import com.soyle.stories.di.resolve
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.MenuItem
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import tornadofx.*

class CharacterConflict : View() {

    private val viewListener = resolve<CharacterConflictViewListener>()
    private val model = resolve<CharacterConflictModel>()

    override val root: Parent = form {
        fieldset(labelPosition = Orientation.VERTICAL) {
            vbox {
                responsiveBox(hSpacing = 8.0, vSpacing = 8.0) {
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
                responsiveBox(hSpacing = 8.0, vSpacing = 8.0) {
                    visibleWhen { model.selectedPerspectiveCharacter.isNotNull }
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
    }

    init {
        model.invalidatedProperty().onChange {
            getValidStateIfInvalid(it)
        }
        getValidStateIfInvalid(model.invalidated)
    }

    private fun getValidStateIfInvalid(invalidated: Boolean) {
        if (invalidated) viewListener.getValidState(null)
    }

}