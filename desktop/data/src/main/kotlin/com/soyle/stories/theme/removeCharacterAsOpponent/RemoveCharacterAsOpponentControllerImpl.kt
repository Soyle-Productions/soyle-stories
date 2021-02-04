package com.soyle.stories.theme.removeCharacterAsOpponent

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.removeCharacterAsOpponent.RemoveCharacterAsOpponent
import java.util.*

class RemoveCharacterAsOpponentControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val removeCharacterAsOpponent: RemoveCharacterAsOpponent,
    private val removeCharacterAsOpponentOutput: RemoveCharacterAsOpponent.OutputPort
) : RemoveCharacterAsOpponentController {
    override fun removeCharacterAsOpponent(themeId: String, perspectiveCharacterId: String, opponentId: String) {
        val request = RemoveCharacterAsOpponent.RequestModel(
            UUID.fromString(themeId),
            UUID.fromString(perspectiveCharacterId),
            UUID.fromString(opponentId)
        )
        threadTransformer.async {
            removeCharacterAsOpponent.invoke(
                request, removeCharacterAsOpponentOutput
            )
        }
    }
}