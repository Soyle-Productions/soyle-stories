package com.soyle.stories.theme.removeSymbolicItem

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemoveSymbolicItem
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemovedSymbolicItem
import kotlin.coroutines.coroutineContext

class RemoveSymbolicItemNotifier : Notifier<RemoveSymbolicItem.OutputPort>(), RemoveSymbolicItem.OutputPort {

    override suspend fun symbolicItemsRemoved(response: List<RemovedSymbolicItem>) {
        notifyAll { it.symbolicItemsRemoved(response) }
    }
}