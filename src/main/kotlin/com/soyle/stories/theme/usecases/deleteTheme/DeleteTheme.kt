package com.soyle.stories.theme.usecases.deleteTheme

import java.util.*

interface DeleteTheme {

    suspend operator fun invoke(themeId: UUID, output: OutputPort)

    interface OutputPort {
        fun themeDeleted(response: DeletedTheme)
    }

}