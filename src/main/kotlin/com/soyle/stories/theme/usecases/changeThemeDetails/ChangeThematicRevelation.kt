package com.soyle.stories.theme.usecases.changeThemeDetails

import java.util.*

interface ChangeThematicRevelation {

    suspend operator fun invoke(themeId: UUID, revelation: String, output: OutputPort)

    class ResponseModel(
        val changedThematicRevelation: ChangedThematicRevelation
    )

    interface OutputPort {
        suspend fun thematicRevelationChanged(response: ResponseModel)
    }

}