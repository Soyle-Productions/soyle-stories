package com.soyle.stories.theme.usecases.listThemes

import java.util.*

interface ListThemes {

    suspend operator fun invoke(projectId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun themesListed(response: ThemeList)
    }

}