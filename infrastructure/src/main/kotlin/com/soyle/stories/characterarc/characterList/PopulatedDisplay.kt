package com.soyle.stories.characterarc.characterList

import com.soyle.stories.characterarc.planCharacterArcDialog.planCharacterArcDialog
import com.soyle.stories.common.makeEditable
import com.soyle.stories.di.resolve
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

    private val model by inject<CharacterListModel>()
    private var treeView: TreeView<Any?> by singleAssign()
    private val characterListViewListener = resolve<CharacterListViewListener>()

    private val characterContextMenu = ContextMenu().apply {
        item("Rename") {
            action {
                val selectedItem = model.selectedItem.value
                val selectedTreeItem = treeView.selectionModel.selectedItems.singleOrNull() ?: return@action
                if (selectedItem is CharacterTreeItemViewModel && selectedTreeItem.value == selectedItem) {
                    treeView.edit(selectedTreeItem)
                }
            }
        }
        item("New Character Arc") {
            action {
                val selectedItem = model.selectedItem.value
                if (selectedItem is CharacterTreeItemViewModel) {
                    planCharacterArcDialog(selectedItem.id, currentStage)
                }
            }
        }
        item("Delete") {
            action {
                val selectedItem = model.selectedItem.value
                if (selectedItem is CharacterTreeItemViewModel) {
                    confirmDeleteCharacter(selectedItem.id, selectedItem.name, characterListViewListener)
                }
            }
        }
    }
    private val characterArcContextMenu = ContextMenu().apply {
        item("Rename") {
            action {
                val selectedItem = model.selectedItem.value
                val selectedTreeItem = treeView.selectionModel.selectedItems.singleOrNull() ?: return@action
                if (selectedItem is CharacterArcItemViewModel && selectedTreeItem.value == selectedItem) {
                    treeView.edit(selectedTreeItem)
                }
            }
        }
        item("Base Story Structure") {
            action {
                val selectedItem = model.selectedItem.value
                if (selectedItem is CharacterArcItemViewModel) {
                    characterListViewListener.openBaseStoryStructureTool(selectedItem.characterId, selectedItem.themeId)
                }
            }
        }
        item("Compare Characters") {
            action {
                val selectedItem = model.selectedItem.value
                if (selectedItem is CharacterArcItemViewModel) {
                    characterListViewListener.openCharacterComparison(selectedItem.characterId, selectedItem.themeId)
                }
            }
        }
        item("Delete") {
            action {
                val selectedItem = model.selectedItem.value
                if (selectedItem is CharacterArcItemViewModel) {
                    confirmDeleteCharacterArc(selectedItem.characterId, selectedItem.themeId, selectedItem.name, characterListViewListener)
                }
            }
        }
    }

    override val root = vbox {
        visibleWhen { model.hasCharacters }
        managedProperty().bind(visibleProperty())
        minWidth = 200.0
        minHeight = 100.0
        vgrow = Priority.ALWAYS
        this@PopulatedDisplay.treeView = treeview<Any?>(TreeItem(null)) {
            isShowRoot = false
            vgrow = Priority.ALWAYS
            makeEditable { newName, oldValue ->
                // rename item
                when (oldValue) {
                    is CharacterTreeItemViewModel -> characterListViewListener.renameCharacter(oldValue.id, newName)
                    is CharacterArcItemViewModel -> characterListViewListener.renameCharacterArc(oldValue.characterId, oldValue.themeId, newName)
                }

                oldValue
            }
            bindSelected(model.selectedItem)
            model.characterTreeItems.set(root.children)
            model.selectedItem.onChange {
                contextMenu = when (it) {
                    is CharacterTreeItemViewModel -> characterContextMenu
                    is CharacterArcItemViewModel -> characterArcContextMenu
                    else -> null
                }
            }
            cellFormat {
                text = when (it) {
                    is CharacterTreeItemViewModel -> it.name
                    is CharacterArcItemViewModel -> it.name
                    else -> throw IllegalArgumentException("Invalid value type")
                }
            }
            populate { parentItem: TreeItem<Any?> ->
                val parentItemValue = parentItem.value
                if (parentItemValue is CharacterTreeItemViewModel) {
                    parentItem.isExpanded = parentItemValue.isExpanded
                }
                when (parentItemValue) {
                    null -> model.characters
                    is CharacterTreeItemViewModel -> {
                        parentItemValue.arcs
                    }
                    else -> emptyList()
                }
            }
            onDoubleClick {
            }
        }
        this += find<ActionBar>()
    }
}
