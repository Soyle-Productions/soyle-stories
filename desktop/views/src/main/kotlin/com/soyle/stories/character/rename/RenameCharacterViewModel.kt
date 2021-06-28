package com.soyle.stories.character.rename

import com.soyle.stories.domain.character.Character
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import tornadofx.ViewModel

class RenameCharacterViewModel(
    val characterId: Character.Id,
    currentName: String = ""
) : ViewModel() {

    val currentName = bind { SimpleStringProperty(currentName) }
    val locked = SimpleBooleanProperty(false)

}