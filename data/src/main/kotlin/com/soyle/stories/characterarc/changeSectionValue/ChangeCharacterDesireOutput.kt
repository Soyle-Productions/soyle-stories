package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.characterarc.usecases.changeCharacterArcSectionValue.ChangeCharacterDesire

class ChangeCharacterDesireOutput(
    private val changedCharacterDesireReceiver: ChangedCharacterArcSectionValueReceiver
) : ChangeCharacterDesire.OutputPort {

    override suspend fun characterDesireChanged(response: ChangeCharacterDesire.ResponseModel) {
        changedCharacterDesireReceiver.receiveChangedCharacterArcSectionValue(response.changedCharacterDesire)
    }

}