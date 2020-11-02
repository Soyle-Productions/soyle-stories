package com.soyle.stories.theme.moralArgument

import com.soyle.stories.common.Model
import com.soyle.stories.soylestories.ApplicationScope
import javafx.application.Platform

class MoralArgumentState : Model<MoralArgumentScope, MoralArgumentViewModel>(MoralArgumentScope::class) {

    val moralProblemLabel = bind(MoralArgumentViewModel::moralProblemLabel)
    val moralProblemValue = bind(MoralArgumentViewModel::moralProblemValue)
    val themeLineLabel = bind(MoralArgumentViewModel::themeLineLabel)
    val themeLineValue = bind(MoralArgumentViewModel::themeLineValue)
    val thematicRevelationLabel = bind(MoralArgumentViewModel::thematicRevelationLabel)
    val thematicRevelationValue = bind(MoralArgumentViewModel::thematicRevelationValue)
    val perspectiveCharacterLabel = bind(MoralArgumentViewModel::perspectiveCharacterLabel)
    val perspectiveCharacterDisplay = bind {
        it?.selectedPerspectiveCharacter?.characterName ?: it?.noPerspectiveCharacterLabel ?: ""
    }
    val loadingCharacterLabel = bind(MoralArgumentViewModel::loadingPerspectiveCharactersLabel)
    val availablePerspectiveCharacters = bind(MoralArgumentViewModel::availablePerspectiveCharacters)
    val unavailableCharacterMessage = bind(MoralArgumentViewModel::unavailableCharacterMessage)
    val sections = bind(MoralArgumentViewModel::sections)
    val availableSectionTypes = bind(MoralArgumentViewModel::availableSectionTypes)
    val loadingSectionTypesLabel = bind(MoralArgumentViewModel::loadingSectionTypesLabel)
    val unavailableSectionTypeMessage = bind(MoralArgumentViewModel::unavailableSectionTypeMessage)

    override fun viewModel(): MoralArgumentViewModel? {
        return item?.copy(
            availablePerspectiveCharacters = availablePerspectiveCharacters.value,
            availableSectionTypes = availableSectionTypes.value
        )
    }

    override fun updateOrInvalidated(update: MoralArgumentViewModel.() -> MoralArgumentViewModel) {
        item = viewModel()
        super.updateOrInvalidated(update)
    }

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope

}