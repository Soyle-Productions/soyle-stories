package com.soyle.stories.theme.usecases.updateThemeMetaData

import java.util.*

interface RenameTheme {

    suspend operator fun invoke(themeId: UUID, name: String, output: OutputPort)

    interface OutputPort {
        fun themeRenamed(response: RenamedTheme)
    }

}