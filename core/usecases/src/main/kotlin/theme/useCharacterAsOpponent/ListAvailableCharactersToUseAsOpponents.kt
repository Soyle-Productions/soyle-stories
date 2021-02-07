package com.soyle.stories.usecase.theme.useCharacterAsOpponent

import java.util.*

interface ListAvailableCharactersToUseAsOpponents {

    suspend operator fun invoke(themeId: UUID, perspectiveCharacterId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun receiveAvailableCharactersToUseAsOpponents(response: AvailableCharactersToUseAsOpponents)
    }

}