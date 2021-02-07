package com.soyle.stories.theme.changeCharacterPropertyValue

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue

class ChangeCharacterPropertyValueNotifier(
    private val threadTransformer: ThreadTransformer
) : Notifier<ChangeCharacterPropertyValue.OutputPort>(), ChangeCharacterPropertyValue.OutputPort {

    override fun receiveChangeCharacterPropertyValueResponse(response: ChangeCharacterPropertyValue.ResponseModel) {
        threadTransformer.async {
            notifyAll { it.receiveChangeCharacterPropertyValueResponse(response) }
        }
    }

    override fun receiveChangeCharacterPropertyValueFailure(failure: ThemeException) {
        threadTransformer.async {
            notifyAll { it.receiveChangeCharacterPropertyValueFailure(failure) }
        }
    }

}