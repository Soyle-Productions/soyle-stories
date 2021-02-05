package com.soyle.stories.theme.usecases.changeThemeDetails

import java.util.*

interface ChangeThemeLine {

    suspend fun invoke(themeId: UUID, themeLine: String, output: OutputPort)

    class ResponseModel(
        val changedThemeLine: ChangedThemeLine
    )

    interface OutputPort {
        suspend fun themeLineChanged(response: ResponseModel)
    }

}