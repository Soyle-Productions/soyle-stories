package com.soyle.stories.characterarc.characterList

import com.soyle.stories.characterarc.planCharacterArcDialog.planCharacterArcDialog
import com.soyle.stories.di.resolve
import com.soyle.stories.di.resolveLater
import com.soyle.stories.project.ProjectScope
import javafx.geometry.Insets
import javafx.geometry.Pos
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 10:02 AM
 */
internal class ActionBar : View() {

    override val scope: ProjectScope = super.scope as ProjectScope
    private val model by resolveLater<CharacterListModel>()
    private val characterListViewListener = resolve<CharacterListViewListener>()

    override val root = hbox(alignment = Pos.CENTER, spacing = 10.0) {
        isFillHeight = false
        padding = Insets(5.0, 0.0, 5.0, 0.0)
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
            id = "actionBar_delete"
            enableWhen { model.selectedItem.isNotNull }
            action {
                when (val selectedItem = model.selectedItem.value) {
                    is CharacterTreeItemViewModel -> characterListViewListener.removeCharacter(selectedItem.id)
                    is CharacterArcItemViewModel ->
                        confirmDeleteCharacterArc(selectedItem.characterId, selectedItem.themeId, selectedItem.name, characterListViewListener)
                }
            }
            isMnemonicParsing = false
        }
    }
}
