package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.theme.changeCharacterPropertyValue.ChangeCharacterPropertyValue

class ChangeCharacterPropertyValueNotifier(private val threadTransformer: ThreadTransformer) : ChangeCharacterPropertyValue.OutputPort, Notifier<ChangeCharacterPropertyValue.OutputPort>() {
    override fun receiveChangeCharacterPropertyValueFailure(failure: Exception) {
        threadTransformer.async {
            notifyAll { it.receiveChangeCharacterPropertyValueFailure(failure) }
        }
    }

    override fun receiveChangeCharacterPropertyValueResponse(response: ChangeCharacterPropertyValue.ResponseModel) {
        threadTransformer.async {
            notifyAll { it.receiveChangeCharacterPropertyValueResponse(response) }
        }
    }
}