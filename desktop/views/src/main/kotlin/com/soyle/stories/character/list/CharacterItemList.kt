package com.soyle.stories.character.list

import com.soyle.stories.characterarc.components.characterIcon
import com.soyle.stories.common.components.surfaces.surface
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.common.scopedListener
import com.soyle.stories.di.get
import javafx.scene.Parent
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import tornadofx.*

class CharacterItemList : Fragment() {

    private val viewModel = scope.get<CharacterListState>()

    override val root: Parent = surface(
        component = TreeView<CharacterListState.CharacterListItem?>(TreeItem(null)),
        elevation = 8
    ) {
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
            }
        }
    }

    private fun TreeView<CharacterListState.CharacterListItem?>.applyCharacterListBehavior() {
        root.children.bind(viewModel.characters) { characterItem ->
            TreeItem<CharacterListState.CharacterListItem?>(characterItem).apply {
                scopedListener(characterItem.hasNew) { if (it == true) isExpanded = true }
                expandedProperty().onChangeUntil({ value != characterItem }) {
                    if (value == characterItem && it != true) characterItem.hasNew.value = false
                }
                children.bind(characterItem.arcs) { TreeItem(it) }
            }
        }
        bindSelected(viewModel.selectedCharacterListItem)
        scopedListener(selectionModel.selectedItemProperty()) {
            viewModel.selectedCharacterListItem.value = it?.value
        }
        contextmenu {
            scopedListener(viewModel.selectedCharacterListItem) {
                when (it) {
                    is CharacterListState.CharacterListItem.CharacterItem -> items.setAll(
                        characterOptions(
                            scope,
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