package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue

class ChangeCharacterPerspectivePropertyValueNotifier : ChangeCharacterPerspectivePropertyValue.OutputPort, Notifier<ChangeCharacterPerspectivePropertyValue.OutputPort>() {
    override fun receiveChangeCharacterPerspectivePropertyValueFailure(failure: ThemeException) {
        notifyAll { it.receiveChangeCharacterPerspectivePropertyValueFailure(failure) }
    }

    override fun receiveChangeCharacterPerspectivePropertyValueResponse(response: ChangeCharacterPerspectivePropertyValue.ResponseModel) {
        notifyAll { it.receiveChangeCharacterPerspectivePropertyValueResponse(response) }
    }
}