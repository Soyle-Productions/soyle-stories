package com.soyle.stories.common

import javafx.scene.control.TextField
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeView
import tornadofx.*


val TreeView<*>.isEditing: Boolean
    get() = editingItem != null

val <T> TreeView<T>.editingCell: TreeCell<T>?
    get() = properties.getOrDefault("com.soyle.stories.treeView.editingCell", null) as? TreeCell<T>

var <T> TreeView<T>.editValidation: ((String, T?) -> String?)?
    get() = properties.getOrDefault("com.soyle.stories.treeView.editValidation", null) as? ((String, T?) -> String?)
    set(value) {
        properties["com.soyle.stories.treeView.editValidation"] = value
    }

fun <T> TreeView<T>.makeEditable(valid: (String, T?) -> String? = { _, _ -> null }, convertFromString: TreeCell<T>.(String, T?) -> T) {
    val self = this
    isEditable = true
    editValidation = valid
    properties["tornadofx.editSupport"] = fun TreeCell<T>.(eventType: EditEventType, value: T?) {
        val cell = this
        graphic = when (eventType) {
            EditEventType.StartEdit -> {
                self.properties["com.soyle.stories.treeView.editingCell"] = this
                val rollbackText = text
                properties["com.soyle.stories.rollbackText"] = rollbackText
                text = null
                textfield(rollbackText) {
                    fun commit() {
                        val errorMessage = valid(textProperty().get(), item)
                        if (errorMessage == null) {
                            cell.commitEdit(convertFromString.invoke(cell, textProperty().get(), item))
                        } else {
                            val errorDecorator = SimpleMessageDecorator(errorMessage, ValidationSeverity.Error)
                            decorators.toList().forEach { removeDecorator(it) }
                            addDecorator(errorDecorator)
                        }
                    }
                    action {
                        commit()
                    }
                    focusedProperty().onChange {
                        if (! it) {
                            if (text != rollbackText) {
                                commit()
                            } else {
                                cell.cancelEdit()
                            }
                        }
                    }
                    requestFocus()
                    selectAll()
                }
            }
            EditEventType.CancelEdit -> {
                self.properties.remove("com.soyle.stories.treeView.editingCell", this)
                text = properties["com.soyle.stories.rollbackText"] as? String ?: ""
                null
            }
            EditEventType.CommitEdit -> {
                self.properties.remove("com.soyle.stories.treeView.editingCell", this)
                text = (graphic as? TextField)?.text ?: (properties["com.soyle.stories.rollbackText"] as? String) ?: ""
                null
            }
        }
    }
}