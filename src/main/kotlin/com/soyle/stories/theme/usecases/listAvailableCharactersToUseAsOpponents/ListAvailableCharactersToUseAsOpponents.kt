package com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents

import java.util.*

interface ListAvailableCharactersToUseAsOpponents {

    suspend operator fun invoke(themeId: UUID, perspectiveCharacterId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun receiveAvailableCharactersToUseAsOpponents(response: AvailableCharactersToUseAsOpponents)
    }

}