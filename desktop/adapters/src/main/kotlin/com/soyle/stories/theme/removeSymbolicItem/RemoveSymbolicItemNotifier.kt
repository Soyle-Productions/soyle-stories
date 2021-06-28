package com.soyle.stories.theme.removeSymbolicItem

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemoveSymbolicItem
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemovedSymbolicItem

class RemoveSymbolicItemNotifier : Notifier<RemoveSymbolicItem.OutputPort>(), RemoveSymbolicItem.OutputPort {

    override suspend fun symbolicItemsRemoved(response: List<RemovedSymbolicItem>) {
        notifyAll { it.symbolicItemsRemoved(response) }
    }
}