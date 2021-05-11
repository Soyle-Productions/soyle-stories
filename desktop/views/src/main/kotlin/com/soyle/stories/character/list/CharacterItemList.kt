package com.soyle.stories.character.list

import com.soyle.stories.characterarc.characterList.CharacterListItemViewModel
import com.soyle.stories.characterarc.components.characterIcon
import com.soyle.stories.common.boundProperty
import com.soyle.stories.common.components.surfaces.Surface
import com.soyle.stories.common.components.surfaces.surface
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.common.scopedListener
import com.soyle.stories.common.softBind
import com.soyle.stories.di.get
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Parent
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import tornadofx.*
import java.lang.ref.WeakReference

class CharacterItemList : Fragment() {

    private val viewModel = scope.get<CharacterListState>()

    override val root: Parent = surface(
        component = TreeView<CharacterListState.SelectableCharacterListItem?>(TreeItem(null)),
        elevation = 8
    ) {
        applyCharacterListBehavior()
        isShowRoot = false
        cellFormat {
            text = it?.name
            graphic = cache { characterIcon(itemProperty().stringBinding { it?.name }) }
            if (viewModel.selectedCharacterItem.value?.isSameSelectableAs(it) == true) {
                if (selectionModel.selectedItem != treeItem) selectionModel.select(treeItem)
            }
        }
    }

    private fun TreeView<CharacterListState.SelectableCharacterListItem?>.applyCharacterListBehavior() {
        bindSelected(viewModel.selectedCharacterItem)
        bindItems(viewModel.characters)
        contextmenu {
            scopedListener(viewModel.selectedCharacterItem) {
                when (it) {
                    is CharacterListState.SelectableCharacterItem -> items.setAll(characterOptions(scope, it.characterItem))
                    is CharacterListState.SelectableArcItem -> items.setAll(characterArcOptions(scope, it.arcItem))
                    null -> items.clear()
                }
            }
        }
    }

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
    }

}