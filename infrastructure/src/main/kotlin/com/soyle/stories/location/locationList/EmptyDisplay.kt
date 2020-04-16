package com.soyle.stories.location.locationList

import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.di.characterarc.CharacterArcComponent
import com.soyle.stories.di.project.LayoutComponent
import com.soyle.stories.project.layout.Dialog
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import javafx.scene.text.TextAlignment
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 9:54 AM
 */
internal class EmptyDisplay : View() {

    private val model by inject<LocationListModel>()
    private val locationListViewListener = find<LocationListComponent>().locationListViewListener
    private val layoutViewListener = find<LayoutComponent>().layoutViewListener

    override val root = gridpane {
        hiddenWhen { model.hasLocations  }
        managedProperty().bind(visibleProperty())
        minWidth = 200.0
        minHeight = 100.0
        hgrow = Priority.SOMETIMES
        vgrow = Priority.ALWAYS
        columnConstraints.add(
            ColumnConstraints().apply { hgrow = Priority.SOMETIMES; minWidth = 10.0 }
        )
        rowConstraints.addAll(
            RowConstraints().apply { minHeight = 10.0; vgrow = Priority.SOMETIMES },
            RowConstraints().apply { minHeight = 10.0; prefHeight = 30.0; vgrow = Priority.SOMETIMES }
        )
        label("No locations to display") {
            alignment = Pos.CENTER
            textAlignment = TextAlignment.CENTER
            isWrapText = true
            GridPane.setHalignment(this, HPos.CENTER)
            GridPane.setValignment(this, VPos.BOTTOM)
            GridPane.setMargin(this, Insets(0.0, 0.0, 5.0, 0.0))
        }
        button("Create a Location") {
            alignment = Pos.CENTER
            isMnemonicParsing = false
            GridPane.setHalignment(this, HPos.CENTER)
            GridPane.setValignment(this, VPos.TOP)
            GridPane.setRowIndex(this, 1)
            GridPane.setMargin(this, Insets(5.0, 0.0, 0.0, 0.0))
            action {
                layoutViewListener.openDialog(Dialog.CreateLocation)
            }
        }
    }
}
