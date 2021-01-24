package com.soyle.stories.theme.addSymbolDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.addSymbolDialog.components.SymbolicCharacterList
import com.soyle.stories.theme.addSymbolDialog.components.SymbolicLocationList
import com.soyle.stories.theme.addSymbolDialog.components.SymbolicSymbolList
import javafx.scene.Parent
import javafx.scene.control.TabPane
import javafx.stage.Modality
import javafx.stage.StageStyle
import javafx.stage.Window
import tornadofx.*

class AddSymbolDialog : Fragment() {

    override val scope: ProjectScope = super.scope as ProjectScope
    private var subScope: AddSymbolDialogScope? = null


    override val root: Parent = tabpane {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
    }

    private fun makeTabs(scope: AddSymbolDialogScope) {
        val model = scope.get<AddSymbolDialogModel>()
        with (root as TabPane) {
            tab<SymbolicCharacterList>(scope) {
                disableProperty().bind(model.characters.select { it.isEmpty().toProperty() })
            }
            tab<SymbolicLocationList>(scope) {
                disableProperty().bind(model.locations.select { it.isEmpty().toProperty() })
            }
            tab<SymbolicSymbolList>(scope)
        }
    }

    fun show(ownerWindow: Window?, themeId: String, oppositionId: String)
    {
        if (currentStage != null && subScope?.isClosed != true) {
            val subScope = AddSymbolDialogScope(scope, themeId, oppositionId)
            makeTabs(subScope)
            val model = subScope.get<AddSymbolDialogModel>()
            model.invalidatedProperty().onChangeUntil(subScope.isClosedProperty()) {
                if (it != false) subScope.get<AddSymbolDialogViewListener>().getValidState()
            }
            subScope.get<AddSymbolDialogViewListener>().getValidState()
            openModal(modality = Modality.WINDOW_MODAL, stageStyle = StageStyle.UTILITY, escapeClosesWindow = true, owner = ownerWindow)?.apply {
                setOnHidden {
                    subScope.close()
                }
                model.completed.onChange {
                    if (it == true) close()
                }
            }
            this.subScope = subScope
        }
    }
}