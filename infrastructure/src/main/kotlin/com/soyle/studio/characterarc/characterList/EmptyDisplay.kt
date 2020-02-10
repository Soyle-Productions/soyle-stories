package com.soyle.studio.characterarc.characterList

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

    private val model by inject<CharacterListModel>()

    override val root = gridpane {
        hiddenWhen { model.hasCharacters  }
        minWidth = 200.0
        minHeight = 100.0
        hgrow = Priority.SOMETIMES
        columnConstraints.add(
            ColumnConstraints().apply { hgrow = Priority.SOMETIMES; minWidth = 10.0 }
        )
        rowConstraints.addAll(
            RowConstraints().apply { minHeight = 10.0; vgrow = Priority.SOMETIMES },
            RowConstraints().apply { minHeight = 10.0; prefHeight = 30.0; vgrow = Priority.SOMETIMES }
        )
        label("No characters to display") {
            alignment = Pos.CENTER
            textAlignment = TextAlignment.CENTER
            isWrapText = true
            GridPane.setHalignment(this, HPos.CENTER)
            GridPane.setValignment(this, VPos.BOTTOM)
            GridPane.setMargin(this, Insets(0.0, 0.0, 5.0, 0.0))
        }
        button("Create a Character") {
            alignment = Pos.CENTER
            isMnemonicParsing = false
            GridPane.setHalignment(this, HPos.CENTER)
            GridPane.setValignment(this, VPos.TOP)
            GridPane.setRowIndex(this, 1)
            GridPane.setMargin(this, Insets(5.0, 0.0, 0.0, 0.0))
            action {
                //controller.createSection()
            }
        }
    }
}
