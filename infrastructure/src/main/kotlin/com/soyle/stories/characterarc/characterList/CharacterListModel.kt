package com.soyle.stories.characterarc.characterList

import com.soyle.stories.common.bindImmutableList
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.control.TreeItem
import tornadofx.ItemViewModel
import tornadofx.toProperty

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 10:41 AM
 */
class CharacterListModel : ItemViewModel<CharacterListViewModel>(), CharacterListView {

    val selectedItem = SimpleObjectProperty<Any?>(null)
    val characters = bindImmutableList(CharacterListViewModel::characters)
    val characterTreeItems = SimpleListProperty(FXCollections.observableArrayList<TreeItem<Any?>>())
    val hasCharacters: ReadOnlyBooleanProperty = bind { (!item?.characters.isNullOrEmpty()).toProperty() }
    val invalid = SimpleBooleanProperty(true)

    fun viewModel() = CharacterListViewModel(
        characterTreeItems.mapNotNull {
            val itemValue = it.value
            if (itemValue !is CharacterTreeItemViewModel) null
            else {
                CharacterTreeItemViewModel(itemValue.id, itemValue.name, itemValue.imageResource, it.isExpanded, itemValue.arcs)
            }
        }
    )

    override suspend fun displayNewViewModel(list: CharacterListViewModel) {
        item = list
        invalid.set(false)
    }

    override suspend fun invalidate() {
        invalid.set(true)
    }

    override suspend fun getViewModel(): CharacterListViewModel? = viewModel()

}