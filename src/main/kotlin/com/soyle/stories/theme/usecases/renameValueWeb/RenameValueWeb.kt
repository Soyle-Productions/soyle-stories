package com.soyle.stories.theme.usecases.renameValueWeb

import java.util.*

interface RenameValueWeb {

    suspend operator fun invoke(valueWebId: UUID, name: String, output: OutputPort)

    interface OutputPort {
        suspend fun valueWebRenamed(response: RenamedValueWeb)
    }

}