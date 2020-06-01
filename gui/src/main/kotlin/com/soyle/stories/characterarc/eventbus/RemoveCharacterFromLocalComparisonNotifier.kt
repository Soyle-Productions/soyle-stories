package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparison

class RemoveCharacterFromLocalComparisonNotifier : RemoveCharacterFromComparison.OutputPort, Notifier<RemoveCharacterFromComparison.OutputPort>() {
    override fun receiveRemoveCharacterFromComparisonResponse(response: RemoveCharacterFromComparison.ResponseModel) {
        notifyAll { it.receiveRemoveCharacterFromComparisonResponse(response) }
    }

    override fun receiveRemoveCharacterFromComparisonFailure(failure: ThemeException) {
        notifyAll { it.receiveRemoveCharacterFromComparisonFailure(failure) }
    }
}