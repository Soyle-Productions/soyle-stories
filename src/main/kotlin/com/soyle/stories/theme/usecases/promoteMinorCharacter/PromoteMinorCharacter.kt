package com.soyle.stories.theme.usecases.promoteMinorCharacter

import com.soyle.stories.characterarc.usecases.planNewCharacterArc.CreatedCharacterArc
import com.soyle.stories.theme.ThemeException
import java.util.*

interface PromoteMinorCharacter {

    class RequestModel(val themeId: UUID, val characterId: UUID)

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val createdCharacterArc: CreatedCharacterArc
    )

    interface OutputPort {
        fun receivePromoteMinorCharacterResponse(response: ResponseModel)
    }
}