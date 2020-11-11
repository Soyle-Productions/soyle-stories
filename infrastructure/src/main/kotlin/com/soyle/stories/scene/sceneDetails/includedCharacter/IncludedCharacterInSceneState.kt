package com.soyle.stories.scene.sceneDetails.includedCharacter

import com.soyle.stories.common.Model
import com.soyle.stories.soylestories.ApplicationScope
import tornadofx.stringBinding

class IncludedCharacterInSceneState : Model<IncludedCharacterScope, IncludedCharacterInSceneViewModel>(IncludedCharacterScope::class) {

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope

    val removeCharacterLabel
        get() = scope.includedCharactersInSceneState.removeCharacterLabel

    val motivationFieldLabel
        get() = scope.includedCharactersInSceneState.motivationFieldLabel

    val resetMotivationLabel
        get() = scope.includedCharactersInSceneState.resetMotivationLabel

    val motivationLastChangedLabel
        get() = scope.includedCharactersInSceneState.motivationLastChangedLabel

    val positionOnCharacterArcLabel
        get() = scope.includedCharactersInSceneState.positionOnCharacterArcLabel

    val characterId = bind(IncludedCharacterInSceneViewModel::characterId)
    val characterName = bind(IncludedCharacterInSceneViewModel::characterName)
    val motivation = bind(IncludedCharacterInSceneViewModel::motivation)
    val motivationCanBeReset = bind(IncludedCharacterInSceneViewModel::motivationCanBeReset)
    val previousMotivation = bind(IncludedCharacterInSceneViewModel::previousMotivation)
    val previousMotivationValue = previousMotivation.stringBinding { it?.value }
    val previousMotivationSource = previousMotivation.stringBinding { it?.sourceSceneId }

    val coveredArcSections = bind(IncludedCharacterInSceneViewModel::coveredArcSections)
    val availableCharacterArcSections = bind(IncludedCharacterInSceneViewModel::availableCharacterArcSections)

    override fun viewModel(): IncludedCharacterInSceneViewModel? {
        return item?.copy(availableCharacterArcSections = availableCharacterArcSections.value)
    }

    init {
        item = scope.consumeInitialState()
    }

}