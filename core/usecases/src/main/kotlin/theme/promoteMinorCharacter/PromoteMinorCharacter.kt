package com.soyle.stories.usecase.theme.promoteMinorCharacter

import com.soyle.stories.usecase.character.arc.planNewCharacterArc.CreatedCharacterArc
import java.util.*

interface PromoteMinorCharacter {

    class RequestModel(val themeId: UUID, val characterId: UUID)

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val createdCharacterArc: CreatedCharacterArc
    )

    interface OutputPort {
        suspend fun receivePromoteMinorCharacterResponse(response: ResponseModel)
    }
}