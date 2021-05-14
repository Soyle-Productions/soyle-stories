package com.soyle.stories.character.list

import com.soyle.stories.characterarc.characterList.*
import com.soyle.stories.common.ProjectScopedModel
import com.soyle.stories.common.scopedListener
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem
import tornadofx.*

class CharacterListState : ViewModel() {

    val loading = SimpleBooleanProperty(true)
    val characters = SimpleListProperty<CharacterListItem.CharacterItem>(null)
    val selectedCharacterListItem: ObjectProperty<CharacterListItem?> = SimpleObjectProperty(null)

    sealed class CharacterListItem {
        class CharacterItem(
            val character: ObjectProperty<CharacterItemViewModel>,
            val hasNew: BooleanProperty,
            val arcs: ObservableList<ArcItem>
        ) : CharacterListItem()

        class ArcItem(
            val arc: ObjectProperty<CharacterArcItemViewModel>,
            val isNew: BooleanProperty
            ) : CharacterListItem() {



            }
    }
}