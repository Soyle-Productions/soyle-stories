package com.soyle.stories.theme.useCharacterAsOpponent

import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonOutput
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.UseCharacterAsOpponent

class UseCharacterAsOpponentOutput(
    private val includeCharacterInComparisonNotifier: IncludeCharacterInComparisonOutput,
    private val useCharacterAsOpponentReceiver: CharacterUsedAsOpponentReceiver
) : UseCharacterAsOpponent.OutputPort {

    override suspend fun characterIsOpponent(response: UseCharacterAsOpponent.ResponseModel) {
        response.includedCharacter?.let {
            includeCharacterInComparisonNotifier.receiveIncludeCharacterInComparisonResponse(it)
        }
        useCharacterAsOpponentReceiver.receiveCharacterUsedAsOpponent(response.characterAsOpponent)
    }
}