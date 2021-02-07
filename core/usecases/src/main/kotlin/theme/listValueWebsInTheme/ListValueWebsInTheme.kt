package com.soyle.stories.usecase.theme.listValueWebsInTheme

import java.util.*

interface ListValueWebsInTheme {

    suspend operator fun invoke(themeId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun valueWebsListedInTheme(response: ValueWebList)
    }

}