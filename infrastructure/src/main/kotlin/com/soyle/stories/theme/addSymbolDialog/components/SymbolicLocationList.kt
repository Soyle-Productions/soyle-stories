package com.soyle.stories.theme.addSymbolDialog.components

import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.di.resolve
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialogModel
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialogViewListener
import javafx.scene.Parent
import javafx.scene.control.SelectionMode
import tornadofx.*

class SymbolicLocationList : Fragment("Locations") {

    private val viewListener = resolve<AddSymbolDialogViewListener>()
    private val model = resolve<AddSymbolDialogModel>()

    override val root: Parent = listview<LocationItemViewModel> {
        items.bind(model.locations) { it }
        cellFormat {
            text = it.name
        }
        selectionModel.selectionMode = SelectionMode.SINGLE
        selectionModel.selectedItemProperty().onChange {
            if (it != null) {
                viewListener.selectLocation(it.id)
            }
        }
    }

}