package com.soyle.stories.theme.useCharacterAsOpponent

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonOutput
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.UseCharacterAsOpponent
import kotlin.coroutines.coroutineContext

class UseCharacterAsOpponentOutput(
    private val includeCharacterInComparisonNotifier: IncludeCharacterInComparisonOutput,
    private val opponentCharacterReceiver: OpponentCharacterReceiver
) : UseCharacterAsOpponent.OutputPort {

    override suspend fun characterIsOpponent(response: UseCharacterAsOpponent.ResponseModel) {
        response.includedCharacter?.let {
            includeCharacterInComparisonNotifier.receiveIncludeCharacterInComparisonResponse(it)
        }
        opponentCharacterReceiver.receiveOpponentCharacter(response.characterAsOpponent)
    }
}