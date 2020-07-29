package com.soyle.stories.theme.useCharacterAsOpponent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.UseCharacterAsOpponent
import java.util.*

class UseCharacterAsOpponentControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val useCharacterAsOpponent: UseCharacterAsOpponent,
    private val useCharacterAsOpponentOutputPort: UseCharacterAsOpponent.OutputPort
) : UseCharacterAsOpponentController {

    override fun useCharacterAsOpponent(themeId: String, perspectiveCharacterId: String, opponentId: String) {
        val preparedThemeId = UUID.fromString(themeId)
        val preparedPerspectiveCharacterId = UUID.fromString(perspectiveCharacterId)
        val preparedOpponentId = UUID.fromString(opponentId)
        threadTransformer.async {
            useCharacterAsOpponent.invoke(UseCharacterAsOpponent.RequestModel(
                preparedThemeId,
                preparedPerspectiveCharacterId,
                preparedOpponentId
            ), useCharacterAsOpponentOutputPort)
        }
    }

}