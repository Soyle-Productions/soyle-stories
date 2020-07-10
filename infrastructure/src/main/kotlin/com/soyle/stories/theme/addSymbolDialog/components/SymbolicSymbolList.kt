package com.soyle.stories.theme.addSymbolDialog.components

import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.common.NoSelectionModel
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialog
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialogModel
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialogScope
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialogViewListener
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import com.soyle.stories.theme.themeList.SymbolListItemViewModel
import javafx.collections.ObservableList
import javafx.scene.Parent
import javafx.scene.control.SelectionMode
import javafx.scene.control.TreeItem
import javafx.scene.input.KeyCode
import tornadofx.*

class SymbolicSymbolList : Fragment("Custom Symbols") {

    override val scope: AddSymbolDialogScope = super.scope as AddSymbolDialogScope

    private val viewListener = resolve<AddSymbolDialogViewListener>()
    private val model = resolve<AddSymbolDialogModel>()

    override val root: Parent = vbox {
        hyperlink("Create Symbol") {
            action {
                createSymbol()
            }
        }
        separator()
        listview<SymbolListItemViewModel> {
            items.bind(model.symbols) { it }
            cellFormat {
                when (it) {
                    is SymbolListItemViewModel -> {
                        text = it.symbolName
                    }
                }
            }
            selectionModel.selectionMode = SelectionMode.SINGLE
            selectionModel.selectedItemProperty().onChange {
                if (it != null) viewListener.selectSymbol(it.symbolId)
            }
        }
    }

    private fun createSymbol() {
        scope.projectScope.get<CreateSymbolDialog>().show(scope.themeId, scope.oppositionId, currentStage?.owner)
        owningTab?.tabPane?.uiComponent<AddSymbolDialog>()?.close()
    }

}