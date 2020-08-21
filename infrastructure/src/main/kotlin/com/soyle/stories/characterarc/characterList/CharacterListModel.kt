package com.soyle.stories.characterarc.characterList

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.control.TreeItem
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 10:41 AM
 */
class CharacterListModel : Model<ProjectScope, CharacterListViewModel>(ProjectScope::class) {

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope

    val selectedItem = SimpleObjectProperty<Any?>(null)
    val characters = bind(CharacterListViewModel::characters)
    val characterTreeItems = SimpleListProperty(FXCollections.observableArrayList<TreeItem<Any?>>())
    val hasCharacters = characters.select { (! it.isNullOrEmpty()).toProperty() }
    val invalid = SimpleBooleanProperty(true)

    override fun viewModel() = CharacterListViewModel(
        characterTreeItems.mapNotNull {
            val itemValue = it.value
            if (itemValue !is CharacterTreeItemViewModel) null
            else {
                CharacterTreeItemViewModel(itemValue.id, itemValue.name, itemValue.imageResource, it.isExpanded, itemValue.arcs)
            }
        }
    )

    init {
        characters.addListener { observable, oldValue, newValue ->
            println("Character List Model characters updated to size ${newValue?.size}")
        }
        hasCharacters.onChange {
            println("Character List Model has characters: $it")
        }
    }

}