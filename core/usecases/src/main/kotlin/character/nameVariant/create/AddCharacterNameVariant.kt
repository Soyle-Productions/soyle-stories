package com.soyle.stories.usecase.character.nameVariant.create

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.events.CharacterNameVariantAdded
import com.soyle.stories.domain.validation.NonBlankString

interface AddCharacterNameVariant {
    suspend operator fun invoke(characterId: Character.Id, variant: NonBlankString, output: OutputPort)

    class ResponseModel(
        val characterNameVariantAdded: CharacterNameVariantAdded
    )

    fun interface OutputPort {
        suspend fun addedCharacterNameVariant(response: ResponseModel)
    }
}