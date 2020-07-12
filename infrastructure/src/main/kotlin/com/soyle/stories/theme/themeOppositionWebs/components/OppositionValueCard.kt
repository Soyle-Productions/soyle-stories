package com.soyle.stories.theme.themeOppositionWebs.components

import com.soyle.stories.common.components.*
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialog
import com.soyle.stories.theme.themeOppositionWebs.Styles
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebs
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsModel
import com.soyle.stories.theme.valueOppositionWebs.OppositionValueViewModel
import com.soyle.stories.theme.valueOppositionWebs.SymbolicItemViewModel
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsViewListener
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.Property
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import kotlinx.coroutines.flow.flow
import tornadofx.*

internal fun GridPane.oppositionValueCard(index: Int, model: ValueOppositionWebsModel, viewListener: ValueOppositionWebsViewListener) {
    val widthProperty = widthProperty()
    val oppositionValue = model.oppositionValues.select { it.getOrNull(index).toProperty() }
    val oppositionValueId = oppositionValue.stringBinding { it?.oppositionValueId }
    val symbolicItems = observableListOf<SymbolicItemViewModel>()
    oppositionValue.select { it.symbolicItems.toProperty() }.onChangeUntil(isNull(oppositionValue)) {
        if (it == null) symbolicItems.clear()
        else symbolicItems.setAll(it)
    }
    val node = card {
        addClass(Styles.oppositionCard)
        isFillWidth = true
        gridpaneConstraints {
            fillWidth = true
            widthProperty.onChangeUntil(isNull(oppositionValue)) {
                if (oppositionValue.value == null || it == null) return@onChangeUntil
                calculateResponsiveLayout(index, it.toInt())
                applyToNode(this@card)
            }
            calculateResponsiveLayout(index, widthProperty.intValue())
        }
        cardHeader {
            cardName(oppositionValue, model) {
                Priority.SOMETIMES
                setOnAction {
                    val id = oppositionValueId.value ?: return@setOnAction
                    val text = editedText ?: return@setOnAction
                    viewListener.renameOppositionValue(id, text)
                }
            }
            spacer()
            button("Remove") {
                hgrow = Priority.NEVER
                usePrefWidth = true
                graphic = MaterialIconView(MaterialIcon.DELETE_FOREVER, "1.5em")
                widthProperty.onChangeUntil(isNull(oppositionValue)) {
                    if (oppositionValue.value == null || it == null) return@onChangeUntil
                    text = if (it.toInt() < ValueOppositionWebs.smallBoundary) ""
                    else "Remove"
                }
                action {
                    val valueWebId = model.selectedValueWeb.value?.valueWebId ?: return@action
                    val oppositionValueId = oppositionValueId.value ?: return@action
                    viewListener.removeOpposition(valueWebId, oppositionValueId)
                }
            }
        }
        cardBody {
            button("Add Symbol") {
                action {
                    val oppositionValueId = oppositionValueId.value ?: return@action
                    model.scope.projectScope.get<AddSymbolDialog>().show(model.scope.themeId.toString(), oppositionValueId)
                }
            }
            flowpane {
                addClass("chips")
                hgap = 8.0
                vgap = 8.0
                padding = Insets(8.0, 4.0, 4.0, 4.0)
                bindChildren(symbolicItems) {
                    chip(it.itemName.toProperty(), onDelete={
                        val oppositionValueId = oppositionValueId.value ?: return@chip
                        viewListener.removeSymbolicItem(oppositionValueId, it.itemId)
                    }).node
                }
            }
        }
    }
    oppositionValue.onChangeUntil(isNull(oppositionValue)) {
        if (it == null) node.removeFromParent()
    }
}

private fun GridPaneConstraint.calculateResponsiveLayout(index: Int, width: Int) {
    if (width < ValueOppositionWebs.smallBoundary) {
        rowIndex = index
        columnIndex = 0
    } else {
        rowIndex = index / 2
        columnIndex = index % 2
    }
}

private fun Parent.cardName(
    oppositionValue: Property<OppositionValueViewModel?>,
    model: ValueOppositionWebsModel,
    op: EditableText.() -> Unit
): EditableText {
    val oppositionValueName = oppositionValue.stringBinding { it?.oppositionValueName }
    val isErrorSource = oppositionValue.stringBinding { it?.oppositionValueId }.isEqualTo(model.errorSource)

    return editableText(oppositionValueName) {
        if (oppositionValue.value?.isNew == true) show()
        oppositionValueName.onChangeUntil(isNull(oppositionValue)) { hide() }
        onShowing {
            if (isErrorSource.value) { model.errorSource.set(null) }
        }
        isErrorSource.onChangeUntil(isNull(oppositionValue)) {
            errorMessageProperty.value = if (it == true) model.errorMessage.value
            else null
        }
        model.errorMessage.onChangeUntil(isNull(oppositionValue)) {
            errorMessageProperty.value = if (isErrorSource.get()) it
            else null
        }
        op()
    }
}

private fun <T> isNull(property: Property<*>) = fun (t: T): Boolean {
    return property.value == null
}