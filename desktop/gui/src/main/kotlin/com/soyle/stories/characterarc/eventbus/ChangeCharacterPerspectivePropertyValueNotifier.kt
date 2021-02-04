package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue
import kotlin.coroutines.coroutineContext

class ChangeCharacterPerspectivePropertyValueNotifier(
    private val threadTransformer: ThreadTransformer
) : ChangeCharacterPerspectivePropertyValue.OutputPort, Notifier<ChangeCharacterPerspectivePropertyValue.OutputPort>() {
    override fun receiveChangeCharacterPerspectivePropertyValueFailure(failure: ThemeException) {
        threadTransformer.async {
            notifyAll { it.receiveChangeCharacterPerspectivePropertyValueFailure(failure) }
        }
    }

    override suspend fun receiveChangeCharacterPerspectivePropertyValueResponse(response: ChangeCharacterPerspectivePropertyValue.ResponseModel) {
        notifyAll { it.receiveChangeCharacterPerspectivePropertyValueResponse(response) }
    }
}