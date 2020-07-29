package com.soyle.stories.character.usecases.createPerspectiveCharacter

import com.soyle.stories.character.usecases.buildNewCharacter.CreatedCharacter
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import java.util.*

interface CreatePerspectiveCharacter {

    suspend operator fun invoke(themeId: UUID, name: String, output: OutputPort)

    class ResponseModel(
        val createdCharacter: CreatedCharacter,
        val characterIncludedInTheme: CharacterIncludedInTheme
    )

    interface OutputPort {
        suspend fun createdPerspectiveCharacter(response: ResponseModel)
    }

}