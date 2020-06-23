package com.soyle.stories.theme.usecases.addValueWebToTheme

import java.util.*

interface AddValueWebToTheme {

    suspend operator fun invoke(themeId: UUID, name: String, output: OutputPort)

    interface OutputPort {
        suspend fun addedValueWebToTheme(response: ValueWebAddedToTheme)
    }
}