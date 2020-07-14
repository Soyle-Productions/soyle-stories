package com.soyle.stories.characterarc.characterList.components

import com.soyle.stories.character.usecases.validateCharacterName
import com.soyle.stories.characterarc.characterList.*
import com.soyle.stories.characterarc.characterList.PopulatedDisplay
import com.soyle.stories.characterarc.characterList.PopulatedDisplay.Companion.defaultCharacterImage
import com.soyle.stories.characterarc.planCharacterArcDialog.planCharacterArcDialog
import com.soyle.stories.common.components.*
import com.soyle.stories.common.components.ComponentsStyles.Companion.liftedCard
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import tornadofx.*

internal fun PopulatedDisplay.characterCard(characterItem: CharacterTreeItemViewModel): VBox {
    return card {
        maxHeight = Region.USE_PREF_SIZE
        val card = this
        val bodyExpanded = SimpleBooleanProperty(false)
        toggleClass(liftedCard, bodyExpanded)
        val header = cardHeader {
            characterItem.imageResource.takeIf { it.isNotBlank() }?.let {
                imageview(it)
            } ?: this += MaterialIconView(defaultCharacterImage, "1.5em")
            val nameField = editableText(characterItem.name) {
                root.style { fontSize = 1.25.em }
                onShowing { errorMessage = null }
                setOnAction { _ ->
                    val newName = editedText ?: ""
                    try {
                        validateCharacterName(newName)
                    } catch (e: Exception) {
                        errorMessage = e.localizedMessage ?: "Invalid Character Name"
                        return@setOnAction
                    }
                    characterListViewListener.renameCharacter(characterItem.id, newName)
                    hide()
                }
            }
            spacer()
            vbox(alignment = Pos.CENTER_RIGHT) {
                val buttonContainer = this
                val optionsBtn = buttonCombo {
                    graphic = MaterialIconView(MaterialIcon.MORE_VERT)
                    buttonContainer.visibleProperty().onChange { isFocusTraversable = it }
                    item("Rename") {
                        action { nameField.show() }
                    }
                    item("New Character Arc") {
                        action {
                            planCharacterArcDialog(characterItem.id, currentStage)
                        }
                    }
                    item("Delete") {
                        action {
                            confirmDeleteCharacter(characterItem.id, characterItem.name, characterListViewListener)
                        }
                    }
                }
                val spacer = spacer()
                val expandBtn = button {
                    hiddenWhen { characterItem.arcs.isEmpty().toProperty() }
                    managedProperty().bind(visibleProperty())
                    graphic = MaterialIconView(MaterialIcon.EXPAND_MORE)
                    bodyExpanded.onChange {
                        graphic = if (it) MaterialIconView(MaterialIcon.EXPAND_LESS) else MaterialIconView(MaterialIcon.EXPAND_MORE)
                    }
                    buttonContainer.visibleProperty().onChange { isFocusTraversable = it }
                    action {
                        bodyExpanded.set(!bodyExpanded.get())
                    }
                }
                with(spacer) {
                    managedProperty().bind(expandBtn.managedProperty())
                }
                visibleWhen(
                    card.hoverProperty()
                        .or(nameField.focusedProperty())
                        .or(optionsBtn.focusedProperty())
                        .or(expandBtn.focusedProperty())
                        .or(bodyExpanded)
                )
            }
        }
        cardBody {
            style {
                padding = box(0.px, 16.px, 16.px, 16.px)
            }
            visibleProperty().bind(bodyExpanded)
            managedProperty().bind(visibleProperty())
            label("Character Arcs") {
                style { fontSize = 1.1.em }
            }
            characterItem.arcs.forEach {
                hbox {
                    val arcItem = this
                    val arcNameField = editableText(it.name) {
                        onShowing { errorMessage = null }
                        setOnAction { _ ->
                            val newName = editedText ?: ""
                            if (newName.isBlank()) {
                                errorMessage = "Character Arc Name Cannot Be Blank"
                                return@setOnAction
                            }
                            characterListViewListener.renameCharacterArc(characterItem.id, it.themeId, newName)
                            hide()
                        }
                    }
                    val spacer = spacer()
                    val optionsButton = buttonCombo {
                        visibleWhen(arcItem.hoverProperty()
                            .or(arcNameField.focusedProperty())
                            .or(focusedProperty()))
                        managedProperty().bind(visibleProperty())
                        graphic = MaterialIconView(MaterialIcon.MORE_VERT)
                        item("Rename") {
                            action { arcNameField.show() }
                        }
                        item("Base Story Structure") {
                            action {
                                characterListViewListener.openBaseStoryStructureTool(it.characterId, it.themeId)
                            }
                        }
                        item("Delete") {
                            action {
                                confirmDeleteCharacterArc(it.characterId, it.themeId, it.name, characterListViewListener)
                            }
                        }
                    }
                    spacer.visibleProperty().bind(optionsButton.visibleProperty())
                    spacer.managedProperty().bind(spacer.visibleProperty())
                }
            }
        }
    }
}