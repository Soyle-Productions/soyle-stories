package com.soyle.stories.location.locationList

import com.soyle.stories.characterarc.planCharacterArcDialog.planCharacterArcDialog
import com.soyle.stories.common.makeEditable
import com.soyle.stories.di.characterarc.CharacterArcComponent
import com.soyle.stories.di.project.LayoutComponent
import com.soyle.stories.project.layout.Dialog
import javafx.scene.control.*
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 9:59 AM
 */
internal class PopulatedDisplay : View() {

    private val model by inject<LocationListModel>()
    private var treeView: TreeView<LocationItemViewModel?> by singleAssign()
    private val locationListViewListener = find<LocationListComponent>().locationListViewListener
    private val layoutViewListener = find<LayoutComponent>().layoutViewListener

    private val characterContextMenu = ContextMenu().apply {
        item("Rename") {
            action {
                val selectedItem = model.selectedItem.value
                val selectedTreeItem = treeView.selectionModel.selectedItems.singleOrNull() ?: return@action
                if (selectedItem is LocationItemViewModel && selectedTreeItem.value == selectedItem) {
                    treeView.edit(selectedTreeItem)
                }
            }
        }
        item("Delete") {
            action {
                val selectedItem = model.selectedItem.value
                if (selectedItem is LocationItemViewModel) {
                    layoutViewListener.openDialog(Dialog.DeleteLocation(selectedItem.id, selectedItem.name))
                }
            }
        }
    }

    override val root = vbox {
        visibleWhen { model.hasLocations }
        managedProperty().bind(visibleProperty())
        minWidth = 200.0
        minHeight = 100.0
        vgrow = Priority.ALWAYS
        this@PopulatedDisplay.treeView = treeview<LocationItemViewModel?>(TreeItem(null)) {
            isShowRoot = false
            vgrow = Priority.ALWAYS
            makeEditable { newName, oldValue ->
                // rename item
                when (oldValue) {
                    is LocationItemViewModel -> locationListViewListener.renameLocation(oldValue.id, newName)
                }

                oldValue
            }
            bindSelected(model.selectedItem)
            model.selectedItem.onChange {
                contextMenu = when (it) {
                    is LocationItemViewModel -> characterContextMenu
                    else -> null
                }
            }
            cellFormat {
                text = when (it) {
                    is LocationItemViewModel -> it.name
                    else -> throw IllegalArgumentException("Invalid value type")
                }
            }
            populate { parentItem: TreeItem<LocationItemViewModel?> ->
                when (parentItem.value) {
                    null -> model.locations
                    else -> emptyList()
                }
            }
            onDoubleClick {
            }
        }
        this += find<ActionBar>()
    }
}
