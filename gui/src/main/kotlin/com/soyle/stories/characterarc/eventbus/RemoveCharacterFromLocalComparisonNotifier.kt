package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.theme.LocalThemeException
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromLocalComparison

class RemoveCharacterFromLocalComparisonNotifier : RemoveCharacterFromLocalComparison.OutputPort, Notifier<RemoveCharacterFromLocalComparison.OutputPort>() {
    override fun receiveRemoveCharacterFromLocalComparisonFailure(failure: LocalThemeException) {
        notifyAll { it.receiveRemoveCharacterFromLocalComparisonFailure(failure) }
    }

    override fun receiveRemoveCharacterFromLocalComparisonResponse(response: RemoveCharacterFromLocalComparison.ResponseModel) {
        notifyAll { it.receiveRemoveCharacterFromLocalComparisonResponse(response) }
    }
}