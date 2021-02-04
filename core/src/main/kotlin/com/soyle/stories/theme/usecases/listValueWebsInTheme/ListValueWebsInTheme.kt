package com.soyle.stories.theme.usecases.listValueWebsInTheme

import java.util.*

interface ListValueWebsInTheme {

    suspend operator fun invoke(themeId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun valueWebsListedInTheme(response: ValueWebList)
    }

}