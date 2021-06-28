package com.soyle.stories.theme.renameOppositionValue

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.theme.oppositionValue.RenamedOppositionValue
import com.soyle.stories.usecase.theme.renameOppositionValue.RenameOppositionValue

class RenameOppositionValueNotifier : Notifier<RenameOppositionValue.OutputPort>(), RenameOppositionValue.OutputPort {

    override suspend fun oppositionValueRenamed(response: RenamedOppositionValue) {
        notifyAll { it.oppositionValueRenamed(response) }
    }

}