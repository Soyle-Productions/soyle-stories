package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.characterarc.usecases.changeCharacterArcSectionValue.ChangeCharacterArcSectionValue
import com.soyle.stories.characterarc.usecases.changeCharacterArcSectionValue.ChangeCharacterDesire
import com.soyle.stories.characterarc.usecases.changeCharacterArcSectionValue.ChangeCharacterMoralWeakness
import com.soyle.stories.characterarc.usecases.changeCharacterArcSectionValue.ChangeCharacterPsychologicalWeakness

class ChangeCharacterArcSectionValueOutput(
    private val changedCharacterArcSectionValueReceiver: ChangedCharacterArcSectionValueReceiver
) : ChangeCharacterDesire.OutputPort, ChangeCharacterMoralWeakness.OutputPort,
    ChangeCharacterPsychologicalWeakness.OutputPort, ChangeCharacterArcSectionValue.OutputPort {

    override suspend fun characterArcSectionValueChanged(response: ChangeCharacterArcSectionValue.ResponseModel) {
        changedCharacterArcSectionValueReceiver.receiveChangedCharacterArcSectionValue(response.changedCharacterArcSectionValue)
    }

    override suspend fun characterDesireChanged(response: ChangeCharacterDesire.ResponseModel) {
        changedCharacterArcSectionValueReceiver.receiveChangedCharacterArcSectionValue(response.changedCharacterDesire)
    }

    override suspend fun characterMoralWeaknessChanged(response: ChangeCharacterMoralWeakness.ResponseModel) {
        changedCharacterArcSectionValueReceiver.receiveChangedCharacterArcSectionValue(response.changedCharacterMoralWeakness)
    }

    override suspend fun characterPsychologicalWeaknessChanged(response: ChangeCharacterPsychologicalWeakness.ResponseModel) {
        changedCharacterArcSectionValueReceiver.receiveChangedCharacterArcSectionValue(response.changedCharacterPsychologicalWeakness)
    }

}