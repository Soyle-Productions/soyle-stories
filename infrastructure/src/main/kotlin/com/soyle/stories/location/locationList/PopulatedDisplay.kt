package com.soyle.stories.location.locationList

import com.soyle.stories.common.makeEditable
import com.soyle.stories.di.resolve
import com.soyle.stories.location.deleteLocationDialog.deleteLocationDialog
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.LayoutViewListener
import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 9:59 AM
 */
internal class PopulatedDisplay : View() {

    override val scope: ProjectScope = super.scope as ProjectScope

    private val model by inject<LocationListModel>()
    private var treeView: TreeView<LocationItemViewModel?> by singleAssign()
    private val locationListViewListener: LocationListViewListener = resolve()
    private val layoutViewListener: LayoutViewListener = resolve()

    private val locationContextMenu = ContextMenu().apply {
        id = "locationContextMenu"
        item("Open") {
            id = "open"
            action {
                val selectedItem = model.selectedItem.value
                if (selectedItem is LocationItemViewModel) {
                    locationListViewListener.openLocationDetails(selectedItem.id)
                }
            }
        }
        item("Rename") {
            id = "rename"
            action {
                val selectedItem = model.selectedItem.value
                val selectedTreeItem = treeView.selectionModel.selectedItems.singleOrNull() ?: return@action
                if (selectedItem is LocationItemViewModel && selectedTreeItem.value == selectedItem) {
                    treeView.edit(selectedTreeItem)
                }
            }
        }
        item("Delete") {
            id = "delete"
            action {
                val selectedItem = model.selectedItem.value
                if (selectedItem is LocationItemViewModel) {
                    deleteLocationDialog(scope, selectedItem)
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
            selectionModel.selectedItemProperty().onChange { model.selectedItem.value = it?.value }
            model.selectedItem.onChange { newSelection -> selectionModel.select(root.children.find { it.value?.id == newSelection?.id }) }
            model.selectedItem.onChange {
                contextMenu = when (it) {
                    is LocationItemViewModel -> locationContextMenu
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
