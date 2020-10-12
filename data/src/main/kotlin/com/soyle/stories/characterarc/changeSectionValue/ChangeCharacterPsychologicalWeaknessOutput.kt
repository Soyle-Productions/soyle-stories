package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.characterarc.usecases.changeCharacterArcSectionValue.ChangeCharacterPsychologicalWeakness

class ChangeCharacterPsychologicalWeaknessOutput(
private val changedCharacterDesireReceiver: ChangedCharacterArcSectionValueReceiver
) : ChangeCharacterPsychologicalWeakness.OutputPort {

    override suspend fun characterPsychologicalWeaknessChanged(response: ChangeCharacterPsychologicalWeakness.ResponseModel) {
        changedCharacterDesireReceiver.receiveChangedCharacterArcSectionValue(response.changedCharacterPsychologicalWeakness)
    }

}