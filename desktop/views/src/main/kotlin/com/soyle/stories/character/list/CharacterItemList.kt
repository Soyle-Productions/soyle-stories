package com.soyle.stories.character.list

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.components.characterIcon
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.surface
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.common.scopedListener
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import javafx.scene.Parent
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import tornadofx.*

class CharacterItemList : Fragment() {

    override val scope: ProjectScope = super.scope as ProjectScope
    private val viewModel = scope.get<CharacterListState>()

    private fun viewCharacterProfile(characterItem: CharacterItemViewModel, originatingCell: TreeCell<*>?)
    {
        viewModel.profileCharacterListNode.set(originatingCell)
        viewModel.profileBeingViewed.set(characterItem)
    }

    override val root: Parent = surface<TreeView<CharacterListState.CharacterListItem?>>(
        elevation = Elevation.getValue(8),
        configure = {
            root = TreeItem(null)
            applyCharacterListBehavior()
            isShowRoot = false
            cellFormat {
                when (it) {
                    is CharacterListState.CharacterListItem.CharacterItem -> {
                        textProperty().cleanBind(it.character.stringBinding { it?.characterName })
                        graphic = characterIcon(it.character.select { it.imageResource.toProperty() })
                        disclosureNode.style { padding = box(7.px, 5.px, 0.px, 5.px) }
                    }
                    is CharacterListState.CharacterListItem.ArcItem -> {
                        text = it.arc.value.name
                        graphic = null
                    }
                    else -> {}
                }
            }
            setOnMouseClicked {
                if (it.clickCount == 2) {
                    val item = selectionModel.selectedItem ?: return@setOnMouseClicked
                    val characterItem = item.value as? CharacterListState.CharacterListItem.CharacterItem
                    if (characterItem != null) {
                        val treeCell = lookupAll(".tree-cell").asSequence()
                            .filterIsInstance<TreeCell<*>>()
                            .find { it.treeItem == item }
                        viewCharacterProfile(characterItem.character.value, treeCell)
                    }
                }
            }
        }
    )

    private fun TreeView<CharacterListState.CharacterListItem?>.applyCharacterListBehavior() {
        root.children.bind(viewModel.characters) { characterItem ->
            TreeItem<CharacterListState.CharacterListItem?>(characterItem).apply {
                scopedListener(characterItem.hasNew) { if (it == true) isExpanded = true }
                expandedProperty().onChangeUntil({ value != characterItem }) {
                    if (value == characterItem && it != true) characterItem.hasNew.value = false
                }
                children.bind(characterItem.arcs) {
                    TreeItem(it)
                }
            }
        }
        bindSelected(viewModel.selectedCharacterListItem)
        contextmenu {
            scopedListener(viewModel.selectedCharacterListItem) {
                when (it) {
                    is CharacterListState.CharacterListItem.CharacterItem -> items.setAll(
                        characterOptions(
                            scope,
                            { itemViewModel ->
                                val treeCell = lookupAll(".tree-cell").asSequence()
                                    .filterIsInstance<TreeCell<*>>()
                                    .find { it.treeItem.value == itemViewModel }
                                viewCharacterProfile(it.character.value, treeCell)
                            },
                            it.character.value
                        )
                    )
                    is CharacterListState.CharacterListItem.ArcItem -> items.setAll(characterArcOptions(scope, it.arc.value))
                    null -> items.clear()
                }
            }
        }
    }/*

    private fun TreeView<CharacterListState.SelectableCharacterListItem?>.bindItems(characters: SimpleListProperty<CharacterListItemViewModel>) {
        val treeItemsById = mutableMapOf<String, TreeItem<CharacterListState.SelectableCharacterListItem?>>()
        scopedListener(characters) { newCharacters ->
            when (newCharacters) {
                null -> {
                    root.children.clear()
                    treeItemsById.clear()
                }
                else -> {
                    val newItems = newCharacters.associate { characterListItem ->
                        val treeItem = treeItemsById.getOrPut(characterListItem.item.characterId) { TreeItem() }
                        treeItem.value = CharacterListState.SelectableCharacterItem(characterListItem.item)
                        treeItem.children.setAll(characterListItem.arcs.map { TreeItem(CharacterListState.SelectableArcItem(it)) })
                        characterListItem.item.characterId to treeItem
                    }
                    val currentSelection = viewModel.selectedCharacterItem.value
                    root.children.setAll(newItems.values)
                    viewModel.selectedCharacterItem.set(currentSelection)
                    treeItemsById.clear()
                    treeItemsById.putAll(newItems)
                }
            }
        }
    }*/

}