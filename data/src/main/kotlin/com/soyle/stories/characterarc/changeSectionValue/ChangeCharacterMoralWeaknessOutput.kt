package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.theme.usecases.changeCharacterArcSectionValue.ChangeCharacterMoralWeakness

class ChangeCharacterMoralWeaknessOutput(
    private val changedCharacterDesireReceiver: ChangedCharacterArcSectionValueReceiver
) : ChangeCharacterMoralWeakness.OutputPort {

    override suspend fun characterMoralWeaknessChanged(response: ChangeCharacterMoralWeakness.ResponseModel) {
        changedCharacterDesireReceiver.receiveChangedCharacterArcSectionValue(response.changedCharacterMoralWeakness)
    }
}