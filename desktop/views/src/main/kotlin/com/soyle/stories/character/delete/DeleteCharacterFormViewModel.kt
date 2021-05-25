package com.soyle.stories.character.delete

import com.soyle.stories.domain.character.Character
import javafx.beans.property.SimpleStringProperty
import tornadofx.ViewModel
import tornadofx.stringBinding

class DeleteCharacterFormViewModel(
    val characterId: Character.Id,
    name: String
) : ViewModel() {

    val characterName = SimpleStringProperty(name)

    val title = SimpleStringProperty("Confirm")
    val header = characterName.stringBinding { "Delete $it?" }
    val message = SimpleStringProperty("Are you sure you want to delete this character?")

    val deleteButtonLabel = SimpleStringProperty("Delete")
    val cancelButtonLabel = SimpleStringProperty("Cancel")
}