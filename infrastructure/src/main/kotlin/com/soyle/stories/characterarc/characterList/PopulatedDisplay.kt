package com.soyle.stories.characterarc.characterList

import com.soyle.stories.character.usecases.validateCharacterName
import com.soyle.stories.characterarc.Styles.Companion.defaultCharacterImage
import com.soyle.stories.characterarc.characterList.components.characterCard
import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.characterarc.planCharacterArcDialog.planCharacterArcDialog
import com.soyle.stories.common.components.*
import com.soyle.stories.common.components.ComponentsStyles.Companion.arrowIconButton
import com.soyle.stories.common.components.ComponentsStyles.Companion.iconButton
import com.soyle.stories.common.makeEditable
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.geometry.VPos
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 9:59 AM
 */
internal class PopulatedDisplay : View() {

    private val model by inject<CharacterListModel>()
    private var treeView: TreeView<Any?> by singleAssign()
    internal val characterListViewListener = resolve<CharacterListViewListener>()

    private val viewStyle = SimpleBooleanProperty(true)

    private val characterContextMenu = ContextMenu().apply {
        item("Rename") {
            id = "rename"
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
            id = "delete"
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
                    characterListViewListener.openCharacterValueComparison(selectedItem.themeId)
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
        hbox(spacing = 10.0) {
            padding = Insets(8.0, 8.0, 8.0,  8.0)
            button("New Character") {
                isDisable = false
                action {
                    createCharacterDialog(scope as ProjectScope)
                }
                isMnemonicParsing = false
            }
            spacer()
            buttonCombo("View As ...") {
                graphic = MaterialIconView(MaterialIcon.VISIBILITY, "1.5em")
                checkmenuitem("List") {
                    graphic = MaterialIconView(MaterialIcon.VIEW_LIST, "1.5em")
                    viewStyle.onChange { isSelected = it }
                    action {
                        viewStyle.set(true)
                        // because checkmenuitem's can be deselected.  This prevents it from being deselected.
                        isSelected = viewStyle.get()
                    }
                    isSelected = viewStyle.get()
                }
                checkmenuitem("Grid") {
                    graphic = MaterialIconView(MaterialIcon.VIEW_MODULE, "1.5em")
                    viewStyle.onChange { isSelected = !it }
                    action {
                        viewStyle.set(false)
                        isSelected = !viewStyle.get()
                    }
                    isSelected = !viewStyle.get()
                }
            }
        }
        this@PopulatedDisplay.treeView = treeview<Any?>(TreeItem(null)) {
            isShowRoot = false
            vgrow = Priority.ALWAYS
            visibleWhen(viewStyle)
            managedProperty().bind(visibleProperty())
            makeEditable({ newName, oldValue ->

                if (newName.isBlank()) "Name cannot be blank"
                else null

            }) { newName, oldValue ->

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
                when (it) {
                    is CharacterTreeItemViewModel -> {
                        text = it.name
                        graphic = it.imageResource.takeIf { it.isNotBlank() }?.let {
                            imageview(it)
                        } ?: MaterialIconView(defaultCharacterImage, "1.5em")
                    }
                    is CharacterArcItemViewModel -> {
                        text = it.name
                        graphic = null
                    }
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
        }
        flowpane {
            visibleWhen(viewStyle.not())
            managedProperty().bind(visibleProperty())
            vgrow = Priority.ALWAYS
            hgap = 8.0
            vgap = 8.0
            padding = Insets(8.0)
            rowValignment = VPos.TOP
            repeat(model.characters.size) { i ->
                characterCard(this, model.characters.select { it.getOrNull(i).toProperty() })
            }
            model.characters.addListener { _, oldValue, newValue ->
                val oldSize = oldValue?.size ?: 0
                val newSize = newValue?.size ?: 0
                if (newSize > oldSize) {
                    repeat(newSize - oldSize) { i ->
                        characterCard(this, model.characters.select { it.getOrNull(i + oldSize).toProperty() })
                    }
                }
            }
        }
        this += find<ActionBar> {
            root.visibleWhen(viewStyle)
            root.managedProperty().bind(visibleProperty())
        }
    }
}
