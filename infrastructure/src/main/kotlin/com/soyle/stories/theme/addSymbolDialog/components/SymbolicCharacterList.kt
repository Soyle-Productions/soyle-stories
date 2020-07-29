package com.soyle.stories.theme.addSymbolDialog.components

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.di.resolve
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialogModel
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialogViewListener
import javafx.scene.Parent
import javafx.scene.control.SelectionMode
import tornadofx.*

class SymbolicCharacterList : Fragment("Characters") {

    private val viewListener = resolve<AddSymbolDialogViewListener>()
    private val model = resolve<AddSymbolDialogModel>()

    override val root: Parent = listview<CharacterItemViewModel> {
        items.bind(model.characters) { it }
        cellFormat {
            text = it.characterName
        }
        selectionModel.selectionMode = SelectionMode.SINGLE
        selectionModel.selectedItemProperty().onChange {
            if (it != null) {
                viewListener.selectCharacter(it.characterId)
            }
        }
    }

}