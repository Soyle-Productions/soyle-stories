package com.soyle.studio.characterarc.characterList

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

    override val root = hbox(alignment = Pos.CENTER, spacing = 10.0) {
        isFillHeight = false
        padding = Insets(5.0, 0.0, 5.0, 0.0)
        button("New Character") {
            isDisable = false
            action {
                //controller.createSection()
            }
            isMnemonicParsing = false
        }
        button("New Character Arc") {
            enableWhen { model.selectedItem.isNotNull }
            action {
                val selectedItem = model.selectedItem.value
                if (selectedItem is CharacterItemViewModel) {
                    //controller.createScene(selectedItem, null)
                }
            }
            isMnemonicParsing = false
        }
        button("Delete") {
            enableWhen { model.selectedItem.isNotNull }
            action {

            }
            isMnemonicParsing = false
        }
    }
}
