package com.soyle.stories.usecase.character.createPerspectiveCharacter

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.buildNewCharacter.CreatedCharacter
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import java.util.*

interface CreatePerspectiveCharacter {

    suspend operator fun invoke(themeId: UUID, name: NonBlankString, output: OutputPort)

    class ResponseModel(
        val createdCharacter: CreatedCharacter,
        val characterIncludedInTheme: CharacterIncludedInTheme
    )

    interface OutputPort {
        suspend fun createdPerspectiveCharacter(response: ResponseModel)
    }

}