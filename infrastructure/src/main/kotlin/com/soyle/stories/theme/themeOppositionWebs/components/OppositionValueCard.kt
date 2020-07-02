package com.soyle.stories.theme.themeOppositionWebs.components

import com.soyle.stories.common.components.PopOutEditBox
import com.soyle.stories.common.components.popOutEditBox
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.theme.themeOppositionWebs.Styles
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsModel
import com.soyle.stories.theme.valueOppositionWebs.OppositionValueViewModel
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsViewListener
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Popup
import tornadofx.*
import tornadofx.Stylesheet.Companion.contextMenu

internal fun GridPane.oppositionValueCard(index: Int, model: ValueOppositionWebsModel, viewListener: ValueOppositionWebsViewListener) {
    val widthProperty = widthProperty()
    val oppositionValue = model.oppositionValues.select { it.getOrNull(index).toProperty() }
    val oppositionValueId = oppositionValue.stringBinding { it?.oppositionValueId }
    val oppositionValueName = oppositionValue.stringBinding { it?.oppositionValueName }
    val isEditing = model.editingProperty.isEqualTo(oppositionValueId)
    val isErrorSource = oppositionValueId.isEqualTo(model.errorSource)
    val node = vbox {
        addClass(Styles.oppositionCard)
        isFillWidth = true
        gridpaneConstraints {
            fillWidth = true
            widthProperty.onChange {
                if (it < 300) {
                    rowIndex = index
                    columnIndex = 0
                    applyToNode(this@vbox)
                } else {
                    rowIndex = index / 2
                    columnIndex = index % 2
                    applyToNode(this@vbox)
                }
            }
            if (widthProperty.value < 300) {
                rowIndex = index
                columnIndex = 0
            } else {
                rowIndex = index / 2
                columnIndex = index % 2
            }
        }
        hbox(spacing = 5.0, alignment = Pos.CENTER_LEFT) {
            padding = Insets(5.0)
            hyperlink(oppositionValueName) {
                action {
                    model.editingProperty.set(oppositionValueId.valueSafe)
                }
                popOutEditBox(textProperty()) {
                    setOnAction {
                        val id = oppositionValueId.value ?: return@setOnAction
                        viewListener.renameOppositionValue(id, textInput.text)
                    }
                    setOnCloseRequest {
                        if (isEditing.value) {
                            model.editingProperty.set(null)
                        }
                    }
                    oppositionValueName.onChange {
                        hide()
                    }
                    isEditing.onChange {
                        if (it) {
                            if (isErrorSource.value) {
                                model.errorSource.set(null)
                            }
                            popup()
                        }
                    }
                }
                isErrorSource.onChange {
                    val decoratedNode = popOutEditBox?.takeIf { it.isShowing }?.textInput ?: this
                    println("is error source $it")
                    println("decorated node: $decoratedNode")
                    decoratedNode.decorators.toList().forEach { it.undecorate(decoratedNode) }
                    if (it) {
                        decoratedNode.addDecorator(SimpleMessageDecorator(model.errorMessage.value ?: "", ValidationSeverity.Error))
                    }
                }
            }
            spacer()
            button("Add Symbol") {
                hgrow = Priority.NEVER
            }
        }
    }
    oppositionValue.onChangeUntil({ it == null }) {
        if (it == null) node.removeFromParent()
    }
    if (oppositionValue.value?.isNew == true) model.editingProperty.set(oppositionValueId.valueSafe)
}