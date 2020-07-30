package com.soyle.stories.theme.usecases.updateThemeMetaData

import java.util.*

interface ChangeCentralConflict {

    suspend operator fun invoke(themeId: UUID, centralConflict: String, output: OutputPort)

    class ResponseModel(
        val themeWithChangedCentralConflict: ThemeWithCentralConflictChanged
    )

    interface OutputPort {
        suspend fun centralConflictChanged(response: ResponseModel)
    }

}