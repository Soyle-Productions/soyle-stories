package com.soyle.stories.layout.removeToolsWithId

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.removeToolsWithId.RemoveToolsWithId

class RemoveToolsWithIdNotifier(
    private val threadTransformer: ThreadTransformer
) : Notifier<RemoveToolsWithId.OutputPort>(), RemoveToolsWithId.OutputPort {
    override fun toolsRemovedWithId(response: GetSavedLayout.ResponseModel) {
        threadTransformer.async {
            notifyAll { it.toolsRemovedWithId(response) }
        }
    }

    override fun failedToRemoveToolsWithId(failure: Exception) {
        threadTransformer.async {
            notifyAll { it.failedToRemoveToolsWithId(failure) }
        }
    }
}