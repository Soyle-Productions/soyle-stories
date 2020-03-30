package com.soyle.stories.characterarc.characterList

import com.soyle.stories.characterarc.planCharacterArcDialog.planCharacterArcDialog
import com.soyle.stories.di.characterarc.CharacterArcComponent
import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeItem
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 9:59 AM
 */
internal class PopulatedDisplay : View() {

    private val model by inject<CharacterListModel>()
    private val characterListViewListener = find<CharacterArcComponent>().characterListViewListener

    private val characterContextMenu = ContextMenu().apply {
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
                    characterListViewListener.removeCharacter(selectedItem.id)
                }
            }
        }
    }
    private val characterArcContextMenu = ContextMenu().apply {
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
                    characterListViewListener.removeCharacterArc(selectedItem.characterId, selectedItem.themeId)
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
        treeview<Any?>(TreeItem(null)) {
            isShowRoot = false
            vgrow = Priority.ALWAYS
            bindSelected(model.selectedItem)
            model.characterTreeItems.set(root.children)
            model.selectedItem.onChange {
                contextMenu = when (it) {
                    is CharacterTreeItemViewModel -> characterContextMenu
                    is CharacterArcItemViewModel -> characterArcContextMenu
                    else -> null
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
