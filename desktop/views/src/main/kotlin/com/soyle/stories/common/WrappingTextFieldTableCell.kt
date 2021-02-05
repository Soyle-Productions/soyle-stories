/**
 * Created by Brendan
 * Date: 3/11/2020
 * Time: 3:15 PM
 */
package com.soyle.stories.common

import javafx.beans.binding.Bindings
import javafx.scene.control.Control
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.text.Text
import javafx.util.converter.DefaultStringConverter
import tornadofx.stringBinding

class WrappingTextFieldTableCell<S> : TextFieldTableCell<S, String>(DefaultStringConverter()) {
    private val cellText = Text()
    init {
        prefHeight = Control.USE_COMPUTED_SIZE
        cellText.wrappingWidthProperty().bind(widthProperty().subtract(Bindings.multiply(2.0, graphicTextGapProperty())))
        cellText.textProperty().bind(stringBinding(itemProperty()) { get()?.toString() ?: "" })
    }

    override fun cancelEdit() {
        super.cancelEdit()
        graphic = cellText
    }

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)
        if (!isEmpty && !isEditing) {
            graphic = cellText
        }
    }
}

inline fun <reified S : Any> TableColumn<S, String>.wrapEditable() = apply {
    tableView?.isEditable = true
    isEditable = true
    setCellFactory {
        WrappingTextFieldTableCell<S>()
    }
}