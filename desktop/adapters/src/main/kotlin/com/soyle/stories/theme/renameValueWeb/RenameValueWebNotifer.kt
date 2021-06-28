package com.soyle.stories.theme.renameValueWeb

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.renameValueWeb.RenameValueWeb
import com.soyle.stories.usecase.theme.renameValueWeb.RenamedValueWeb

class RenameValueWebNotifier : Notifier<RenameValueWeb.OutputPort>(), RenameValueWeb.OutputPort {
    override suspend fun valueWebRenamed(response: RenamedValueWeb) {
        notifyAll { it.valueWebRenamed(response) }
    }
}