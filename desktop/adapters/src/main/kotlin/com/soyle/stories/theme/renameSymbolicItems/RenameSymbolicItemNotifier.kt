package com.soyle.stories.theme.renameSymbolicItems

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.renameSymbolicItems.RenameSymbolicItem
import com.soyle.stories.usecase.theme.renameSymbolicItems.RenamedSymbolicItem

class RenameSymbolicItemNotifier : Notifier<RenameSymbolicItem.OutputPort>(), RenameSymbolicItem.OutputPort {

    override suspend fun symbolicItemRenamed(response: List<RenamedSymbolicItem>) {
        notifyAll { it.symbolicItemRenamed(response) }
    }
}