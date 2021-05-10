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
        component = TreeView<CharacterListItemViewModel?>(TreeItem(null)),
        elevation = 8
    ) {
        applyCharacterListBehavior()
        isShowRoot = false
        cellFormat {
            text = it?.item?.characterName
            graphic = cache { characterIcon(itemProperty().stringBinding { it?.item?.characterName ?: "" }) }
            if (viewModel.selectedCharacterItem.value?.item?.characterId == it?.item?.characterId) {
                if (selectionModel.selectedItem != treeItem) selectionModel.select(treeItem)
            }
        }
    }

    private fun TreeView<CharacterListItemViewModel?>.applyCharacterListBehavior() {
        bindSelected(viewModel.selectedCharacterItem)
        bindItems(viewModel.characters)
        contextmenu {
            scopedListener(viewModel.selectedCharacterItem) {
                when (it) {
                    is CharacterListItemViewModel -> items.setAll(characterOptions(scope, it))
                    else -> items?.clear()
                }
            }
        }
    }

    private fun TreeView<CharacterListItemViewModel?>.bindItems(characters: SimpleListProperty<CharacterListItemViewModel>) {
        val treeItemsById = mutableMapOf<String, TreeItem<CharacterListItemViewModel?>>()
        scopedListener(characters) { newCharacters ->
            when (newCharacters) {
                null -> {
                    root.children.clear()
                    treeItemsById.clear()
                }
                else -> {
                    val newItems = newCharacters.associate { characterListItem ->
                        val treeItem = treeItemsById.getOrPut(characterListItem.item.characterId) { TreeItem(characterListItem) }
                        treeItem.value = characterListItem
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