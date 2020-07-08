package com.soyle.stories.theme.renameValueWeb

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.renameValueWeb.RenameValueWeb
import com.soyle.stories.theme.usecases.renameValueWeb.RenamedValueWeb
import kotlin.coroutines.coroutineContext

class RenameValueWebNotifier : Notifier<RenameValueWeb.OutputPort>(), RenameValueWeb.OutputPort {
    override suspend fun valueWebRenamed(response: RenamedValueWeb) {
        notifyAll(coroutineContext) { it.valueWebRenamed(response) }
    }
}