package com.soyle.stories.characterarc.characterList

import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.characterarc.planCharacterArcDialog.planCharacterArcDialog
import com.soyle.stories.di.characterarc.CharacterArcComponent
import javafx.geometry.Insets
import javafx.geometry.Pos
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 10:02 AM
 */
internal class ActionBar : View() {

    private val model by inject<CharacterListModel>()
    private val characterListViewListener = find<CharacterListComponent>().characterListViewListener

    override val root = hbox(alignment = Pos.CENTER, spacing = 10.0) {
        isFillHeight = false
        padding = Insets(5.0, 0.0, 5.0, 0.0)
        button("New Character") {
            isDisable = false
            action {
                createCharacterDialog(currentStage)
            }
            isMnemonicParsing = false
        }
        button("New Character Arc") {
            enableWhen { model.selectedItem.isNotNull }
            action {
                val selectedItem = model.selectedItem.value
                if (selectedItem is CharacterTreeItemViewModel) {
                    planCharacterArcDialog(selectedItem.id, currentStage)
                }
            }
            isMnemonicParsing = false
        }
        button("Delete") {
            enableWhen { model.selectedItem.isNotNull }
            action {
                when (val selectedItem = model.selectedItem.value) {
                    is CharacterTreeItemViewModel ->
                        confirmDeleteCharacter(selectedItem.id, selectedItem.name, characterListViewListener)
                    is CharacterArcItemViewModel ->
                        confirmDeleteCharacterArc(selectedItem.characterId, selectedItem.themeId, selectedItem.name, characterListViewListener)
                }
            }
            isMnemonicParsing = false
        }
    }
}
