package com.soyle.stories.character.list

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.characterList.CharacterListViewModel
import com.soyle.stories.characterarc.characterList.CharacterTreeItemViewModel
import com.soyle.stories.common.Model
import com.soyle.stories.common.ProjectScopedModel
import com.soyle.stories.domain.character.Character
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
    private val _selectedCharacterItemId = SimpleObjectProperty<String?>(null)
    val selectedCharacterItem = bind { vm -> vm?.characters?.find { it.item.characterId == _selectedCharacterItemId.value } }
    init {
        _selectedCharacterItemId.bind(selectedCharacterItem.objectBinding { it?.item?.characterId })
    }

}