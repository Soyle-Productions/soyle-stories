package com.soyle.stories.usecase.theme.renameOppositionValue

import com.soyle.stories.domain.theme.oppositionValue.RenamedOppositionValue
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

interface RenameOppositionValue {

    suspend operator fun invoke(oppositionValueId: UUID, name: NonBlankString, output: OutputPort)

    interface OutputPort {
        suspend fun oppositionValueRenamed(response: RenamedOppositionValue)
    }

}