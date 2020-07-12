package com.soyle.stories.theme.renameSymbolicItems

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.renameSymbolicItems.RenameSymbolicItem
import com.soyle.stories.theme.usecases.renameSymbolicItems.RenamedSymbolicItem
import kotlin.coroutines.coroutineContext

class RenameSymbolicItemNotifier : Notifier<RenameSymbolicItem.OutputPort>(), RenameSymbolicItem.OutputPort {

    override suspend fun symbolicItemRenamed(response: List<RenamedSymbolicItem>) {
        notifyAll(coroutineContext) { it.symbolicItemRenamed(response) }
    }
}