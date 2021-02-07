package com.soyle.stories.usecase.theme.listThemes

import java.util.*

interface ListThemes {

    suspend operator fun invoke(projectId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun themesListed(response: ThemeList)
    }

}