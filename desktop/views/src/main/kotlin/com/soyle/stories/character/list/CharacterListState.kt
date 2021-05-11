package com.soyle.stories.character.list

import com.soyle.stories.characterarc.characterList.*
import com.soyle.stories.common.Model
import com.soyle.stories.common.ProjectScopedModel
import com.soyle.stories.domain.character.Character
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.control.TreeItem
import tornadofx.ViewModel
import tornadofx.objectBinding
import tornadofx.select
import tornadofx.toProperty

class CharacterListState : ProjectScopedModel<CharacterListViewModel>() {

    val characters = bind(CharacterListViewModel::characters)
    private val _selectedCharacterItemId = SimpleObjectProperty<SelectableCharacterListItemId?>(null)
    val selectedCharacterItem: ObjectProperty<SelectableCharacterListItem?> =
        bind { vm ->
            val selectedId = _selectedCharacterItemId.value ?: return@bind null
            vm?.characters?.find {
                it.item.characterId == selectedId.id
            }?.let {
                SelectableCharacterItem(it.item)
            } ?: vm?.characters?.asSequence()?.flatMap { it.arcs.asSequence() }?.find {
                it.themeId == selectedId.id
            }?.let {
                SelectableArcItem(it)
            }
        }

    init {
        _selectedCharacterItemId.bind(selectedCharacterItem.objectBinding { it?.selectableId })
    }

    sealed class SelectableCharacterListItemId(val id: String)
    class SelectableCharacterItemId(id: String) : SelectableCharacterListItemId(id)
    class SelectableArcItemId(id: String) : SelectableCharacterListItemId(id)

    sealed class SelectableCharacterListItem(val selectableId: SelectableCharacterListItemId, val name: String) {

        abstract fun isSameSelectableAs(selectable: SelectableCharacterListItem?): Boolean
    }

    class SelectableCharacterItem(val characterItem: CharacterItemViewModel) :
        SelectableCharacterListItem(SelectableCharacterItemId(characterItem.characterId), characterItem.characterName) {

        override fun isSameSelectableAs(selectable: SelectableCharacterListItem?): Boolean {
            return (selectable as? SelectableCharacterItem)?.characterItem?.characterId == characterItem.characterId
        }
    }

    class SelectableArcItem(val arcItem: CharacterArcItemViewModel) :
        SelectableCharacterListItem(SelectableArcItemId(arcItem.themeId), arcItem.name) {

        override fun isSameSelectableAs(selectable: SelectableCharacterListItem?): Boolean {
            return (selectable as? SelectableArcItem)?.arcItem?.let {
                it.characterId == arcItem.characterId && it.themeId == arcItem.themeId
            } ?: false
        }
    }


}