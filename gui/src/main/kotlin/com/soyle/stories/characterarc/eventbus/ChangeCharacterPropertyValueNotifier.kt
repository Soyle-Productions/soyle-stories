package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue

class ChangeCharacterPropertyValueNotifier : ChangeCharacterPropertyValue.OutputPort, Notifier<ChangeCharacterPropertyValue.OutputPort>() {
    override fun receiveChangeCharacterPropertyValueFailure(failure: ThemeException) {
        notifyAll { it.receiveChangeCharacterPropertyValueFailure(failure) }
    }

    override fun receiveChangeCharacterPropertyValueResponse(response: ChangeCharacterPropertyValue.ResponseModel) {
        notifyAll { it.receiveChangeCharacterPropertyValueResponse(response) }
    }
}