package com.soyle.stories.scene.sceneCharacters.includedCharacterItem

import com.soyle.stories.scene.sceneCharacters.CharacterRoleInScene
import com.soyle.stories.scene.sceneCharacters.IncludedCharacterViewModel
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.layout.HBox
import tornadofx.ItemViewModel
import tornadofx.stringBinding

class IncludedCharacterItemModel : ItemViewModel<IncludedCharacterViewModel>() {
    val iconSource: ObservableValue<String?> = bind(IncludedCharacterViewModel::imageResource)
    val characterName: ObservableValue<String> = bind(IncludedCharacterViewModel::name)
    val roleInScene: Property<CharacterRoleInScene> = bind(IncludedCharacterViewModel::roleInScene)
    val characterRole = roleInScene.stringBinding {
        when (it) {
            CharacterRoleInScene.IncitingCharacter -> "Inciting Character"
            CharacterRoleInScene.OpponentToIncitingCharacter -> "Opponent to Inciting Character"
            null -> null
        }
    }
}