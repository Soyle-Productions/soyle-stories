package com.soyle.stories.theme.useCharacterAsMainOpponent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.UseCharacterAsMainOpponent
import java.util.*

class UseCharacterAsMainOpponentControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val useCharacterAsMainOpponent: UseCharacterAsMainOpponent,
    private val useCharacterAsMainOpponentOutputPort: UseCharacterAsMainOpponent.OutputPort
) : UseCharacterAsMainOpponentController {

    override fun useCharacterAsMainOpponent(themeId: String, perspectiveCharacterId: String, opponentId: String) {
        val request = UseCharacterAsMainOpponent.RequestModel(
            UUID.fromString(themeId),
            UUID.fromString(perspectiveCharacterId),
            UUID.fromString(opponentId)
        )
        threadTransformer.async {
            useCharacterAsMainOpponent.invoke(
                request,
                useCharacterAsMainOpponentOutputPort
            )
        }
    }

}