package com.soyle.stories.theme.useCharacterAsOpponent

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonNotifier
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.UseCharacterAsOpponent
import kotlin.coroutines.coroutineContext

class UseCharacterAsOpponentNotifier(
    private val includeCharacterInComparisonNotifier: IncludeCharacterInComparisonNotifier
) : Notifier<UseCharacterAsOpponent.OutputPort>(), UseCharacterAsOpponent.OutputPort {

    override suspend fun characterIsOpponent(response: UseCharacterAsOpponent.ResponseModel) {
        response.includedCharacter?.let {
            includeCharacterInComparisonNotifier.receiveIncludeCharacterInComparisonResponse(it)
        }
        notifyAll(coroutineContext) { it.characterIsOpponent(response) }
    }
}