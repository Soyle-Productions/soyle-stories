package com.soyle.stories.layout.removeToolsWithId

import com.soyle.stories.common.Notifier
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.removeToolsWithId.RemoveToolsWithId

class RemoveToolsWithIdNotifier : Notifier<RemoveToolsWithId.OutputPort>(), RemoveToolsWithId.OutputPort {
    override fun toolsRemovedWithId(response: GetSavedLayout.ResponseModel) {
        notifyAll { it.toolsRemovedWithId(response) }
    }

    override fun failedToRemoveToolsWithId(failure: Exception) {
        notifyAll { it.failedToRemoveToolsWithId(failure) }
    }
}