package com.soyle.stories.usecase.theme.changeThemeDetails

import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

interface RenameTheme {

    suspend operator fun invoke(themeId: UUID, name: NonBlankString, output: OutputPort)

    interface OutputPort {
        suspend fun themeRenamed(response: RenamedTheme)
    }

}