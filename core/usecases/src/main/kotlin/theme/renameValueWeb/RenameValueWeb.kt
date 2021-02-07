package com.soyle.stories.usecase.theme.renameValueWeb

import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

interface RenameValueWeb {

    suspend operator fun invoke(valueWebId: UUID, name: NonBlankString, output: OutputPort)

    interface OutputPort {
        suspend fun valueWebRenamed(response: RenamedValueWeb)
    }

}