package com.soyle.stories.location.locationList

import com.soyle.stories.di.get
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import com.soyle.stories.location.deleteLocationDialog.deleteLocationDialog
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

    private val model by inject<LocationListModel>()

    override val root = hbox(alignment = Pos.CENTER, spacing = 10.0) {
        isFillHeight = false
        padding = Insets(5.0, 0.0, 5.0, 0.0)
        button("New Location") {
            id = "actionBar_createLocation"
            isDisable = false
            action {
                scope.get<CreateLocationDialog.Factory>().invoke()
            }
            isMnemonicParsing = false
        }
        button("Delete") {
            id = "actionBar_deleteLocation"
            enableWhen { model.selectedItem.isNotNull }
            action {
                val selectedItem = model.selectedItem.value ?: return@action
                deleteLocationDialog(scope, selectedItem)
            }
            isMnemonicParsing = false
        }
    }
}
