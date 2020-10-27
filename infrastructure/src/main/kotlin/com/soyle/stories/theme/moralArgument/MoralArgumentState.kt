package com.soyle.stories.theme.moralArgument

import com.soyle.stories.common.Model
import com.soyle.stories.soylestories.ApplicationScope

class MoralArgumentState : Model<MoralArgumentScope, MoralArgumentViewModel>(MoralArgumentScope::class) {

    val moralProblemLabel = bind(MoralArgumentViewModel::moralProblemLabel)
    val moralProblemValue = bind(MoralArgumentViewModel::moralProblemValue)
    val themeLineLabel = bind(MoralArgumentViewModel::themeLineLabel)
    val themeLineValue = bind(MoralArgumentViewModel::themeLineValue)
    val perspectiveCharacterLabel = bind(MoralArgumentViewModel::perspectiveCharacterLabel)
    val perspectiveCharacterDisplay = bind {
        it?.selectedPerspectiveCharacter?.characterName ?: it?.noPerspectiveCharacterLabel ?: ""
    }
    val loadingItemLabel = bind(MoralArgumentViewModel::loadingPerspectiveCharactersLabel)
    val availablePerspectiveCharacters = bind(MoralArgumentViewModel::availablePerspectiveCharacters)
    val unavailableCharacterMessage = bind(MoralArgumentViewModel::unavailableCharacterMessage)
    val sections = bind(MoralArgumentViewModel::sections)

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope

}