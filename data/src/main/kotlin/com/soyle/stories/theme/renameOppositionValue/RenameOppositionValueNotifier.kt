package com.soyle.stories.theme.renameOppositionValue

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.renameOppositionValue.RenameOppositionValue
import com.soyle.stories.theme.usecases.renameOppositionValue.RenamedOppositionValue
import kotlin.coroutines.coroutineContext

class RenameOppositionValueNotifier : Notifier<RenameOppositionValue.OutputPort>(), RenameOppositionValue.OutputPort {

    override suspend fun oppositionValueRenamed(response: RenamedOppositionValue) {
        notifyAll(coroutineContext) { it.oppositionValueRenamed(response) }
    }

}