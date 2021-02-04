package com.soyle.stories.theme.usecases.changeThemeDetails

import java.util.*

interface ChangeCentralConflict {

    suspend operator fun invoke(themeId: UUID, centralConflict: String, output: OutputPort)

    class ResponseModel(
        val changedCentralConflict: CentralConflictChanged
    )

    interface OutputPort {
        suspend fun centralConflictChanged(response: ResponseModel)
    }

}