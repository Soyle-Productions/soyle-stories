package com.soyle.stories.location.locationList

import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.characterarc.planCharacterArcDialog.planCharacterArcDialog
import com.soyle.stories.di.characterarc.CharacterArcComponent
import com.soyle.stories.di.project.LayoutComponent
import com.soyle.stories.project.layout.Dialog
import javafx.geometry.Insets
import javafx.geometry.Pos
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 10:02 AM
 */
internal class ActionBar : View() {

    private val model by inject<LocationListModel>()
    private val layoutViewListener = find<LayoutComponent>().layoutViewListener

    override val root = hbox(alignment = Pos.CENTER, spacing = 10.0) {
        isFillHeight = false
        padding = Insets(5.0, 0.0, 5.0, 0.0)
        button("New Location") {
            isDisable = false
            action {
                layoutViewListener.openDialog(Dialog.CreateLocation)
            }
            isMnemonicParsing = false
        }
        button("Delete") {
            enableWhen { model.selectedItem.isNotNull }
            action {
                when (val selectedItem = model.selectedItem.value) {
                    is LocationItemViewModel -> layoutViewListener.openDialog(Dialog.DeleteLocation(selectedItem.id, selectedItem.name))
                }
            }
            isMnemonicParsing = false
        }
    }
}
