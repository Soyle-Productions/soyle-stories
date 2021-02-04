package com.soyle.stories.scene.sceneDetails.includedCharacters

import com.soyle.stories.common.components.cacheEachAs
import com.soyle.stories.common.components.fieldLabel
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.scene.sceneDetails.SceneDetailsScope
import com.soyle.stories.scene.sceneDetails.includedCharacter.IncludedCharacterScope
import com.soyle.stories.scene.sceneDetails.includedCharacter.IncludedCharacterView
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.MenuItem
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.Spinner
import tornadofx.*

class IncludedCharactersInSceneView(parent: Parent, val scope: SceneDetailsScope) {

    private val viewListener = scope.get<IncludedCharactersInSceneViewListener>()
    private val state = scope.get<IncludedCharactersInSceneState>()

    private val root: Parent = parent.vbox {
        addClass(com.soyle.stories.soylestories.Styles.section)
        hbox {
            fieldLabel(state.title)
            characterSelectionMenu()
        }
        scrollpane(fitToWidth = true) {
            content = vbox {
                spacing = 8.0
                bindChildren(state.includedCharacterScopes) {
                    it.get<IncludedCharacterView>().root
                }
            }
        }
    }

    private fun Parent.characterSelectionMenu() {
        menubutton {
            textProperty().bind(state.addCharacterLabel)
            addClass("add-character")
            val createNewCharacterItem = item("Create New Character") {
                isDisable = true
            }
            val loadingItem = item("Loading ... ", graphic = ProgressIndicator().apply { style { maxHeight = 2.em } }) {
                isDisable = true
            }
            state.availableCharacters.onChangeUntil(parentProperty().isNull) {
                if (it == null) items.setAll(loadingItem)
                else {
                    items.setAll(createNewCharacterItem)
                    it.forEach {
                        item(it.characterName) {
                            action {
                                viewListener.addCharacter(it.characterId)
                            }
                        }
                    }
                }
            }
            setOnShowing {
                if (state.availableCharacters.value == null) viewListener.getAvailableCharacters()
            }
        }
    }

}