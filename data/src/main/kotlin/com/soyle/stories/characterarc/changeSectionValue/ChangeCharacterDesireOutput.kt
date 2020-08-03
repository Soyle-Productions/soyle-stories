package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.theme.usecases.changeCharacterDesire.ChangeCharacterDesire
import com.soyle.stories.theme.usecases.changeCharacterDesire.ChangedCharacterDesire

class ChangeCharacterDesireOutput(
    private val changedCharacterDesireReceiver: ChangedCharacterDesireReceiver
) : ChangeCharacterDesire.OutputPort {

    override suspend fun characterDesireChanged(response: ChangeCharacterDesire.ResponseModel) {
        changedCharacterDesireReceiver.receiveChangedCharacterDesire(response.changedCharacterDesire)
    }

}