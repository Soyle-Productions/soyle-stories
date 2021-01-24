package com.soyle.stories.theme.usecases.renameOppositionValue

import com.soyle.stories.entities.theme.oppositionValue.RenamedOppositionValue
import java.util.*

interface RenameOppositionValue {

    suspend operator fun invoke(oppositionValueId: UUID, name: String, output: OutputPort)

    interface OutputPort {
        suspend fun oppositionValueRenamed(response: RenamedOppositionValue)
    }

}