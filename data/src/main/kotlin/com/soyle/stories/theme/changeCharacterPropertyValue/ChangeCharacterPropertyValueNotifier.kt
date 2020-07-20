package com.soyle.stories.theme.changeCharacterPropertyValue

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue

class ChangeCharacterPropertyValueNotifier : Notifier<ChangeCharacterPropertyValue.OutputPort>(), ChangeCharacterPropertyValue.OutputPort {

    override fun receiveChangeCharacterPropertyValueResponse(response: ChangeCharacterPropertyValue.ResponseModel) {
        notifyAll { it.receiveChangeCharacterPropertyValueResponse(response) }
    }

    override fun receiveChangeCharacterPropertyValueFailure(failure: ThemeException) {
        notifyAll { it.receiveChangeCharacterPropertyValueFailure(failure) }
    }

}