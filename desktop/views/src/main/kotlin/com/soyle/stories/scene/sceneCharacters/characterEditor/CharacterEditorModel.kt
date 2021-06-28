package com.soyle.stories.scene.sceneCharacters.characterEditor

import com.soyle.stories.common.ProjectScopedModel
import com.soyle.stories.scene.sceneCharacters.CharacterRoleInScene
import com.soyle.stories.scene.sceneCharacters.IncludedCharacterViewModel
import com.soyle.stories.scene.sceneCharacters.includedCharacterItem.IncludedCharacterItemView.Companion.includedCharacterItem
import javafx.beans.property.Property
import tornadofx.*

class CharacterEditorModel : ProjectScopedModel<IncludedCharacterViewModel>() {

    val isIncitingCharacter = bind { (it?.roleInScene == CharacterRoleInScene.IncitingCharacter) }
    val isOpponentCharacter = bind { (it?.roleInScene == CharacterRoleInScene.OpponentToIncitingCharacter) }
    val desire = bind(IncludedCharacterViewModel::desire)
    val motivation = bind(IncludedCharacterViewModel::motivation)
    val previousMotivation = bind(IncludedCharacterViewModel::previousMotivation)
    val hasPreviousMotivation = previousMotivation.booleanBinding { it != null }
    val previousMotivationValue = previousMotivation.select { it?.value.toProperty() }
    val previousMotivationSourceName = previousMotivation.stringBinding { it?.sourceSceneName }
    val availableCharacterArcSections = bind(IncludedCharacterViewModel::availableCharacterArcSections)
    val coveredArcSections = bind(IncludedCharacterViewModel::coveredArcSections)

}