package com.soyle.studio.characterarc.characterList

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import tornadofx.ViewModel
import tornadofx.booleanBinding

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 10:41 AM
 */
class CharacterListModel : ViewModel() {

    val selectedItem = SimpleObjectProperty<Any?>(null)
    val characters = SimpleListProperty(FXCollections.observableArrayList<CharacterItemViewModel>())
    val hasCharacters: ReadOnlyBooleanProperty = SimpleBooleanProperty(false).also {
        it.bind(characters.booleanBinding { it.isNullOrEmpty() })
    }

}