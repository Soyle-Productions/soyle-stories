package com.soyle.stories.theme.changeCharacterChange

import com.soyle.stories.theme.usecases.changeCharacterChange.ChangeCharacterChange

class ChangeCharacterChangeOutput(
    private val changedCharacterChangeReceiver: ChangedCharacterChangeReceiver
) : ChangeCharacterChange.OutputPort {

    override suspend fun characterChangeChanged(response: ChangeCharacterChange.ResponseModel) {
        changedCharacterChangeReceiver.receiveChangedCharacterChange(response.changedCharacterChange)
    }

}