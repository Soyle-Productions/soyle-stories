package com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters

import java.util.*

interface ListAvailablePerspectiveCharacters {

    suspend operator fun invoke(themeId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun receiveAvailablePerspectiveCharacters(response: AvailablePerspectiveCharacters)
    }

}