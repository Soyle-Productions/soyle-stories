package com.soyle.stories.usecase.character.nameVariant.rename

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterException
import com.soyle.stories.domain.character.events.CharacterNameVariantRenamed
import com.soyle.stories.domain.validation.NonBlankString

interface RenameCharacterNameVariant {

    class RequestModel(
        val characterId: Character.Id,
        val currentVariant: NonBlankString,
        val newVariant: NonBlankString
    )

    class ResponseModel(val characterNameVariantRenamed: CharacterNameVariantRenamed)

    suspend operator fun invoke(request: RequestModel, output: OutputPort): CharacterException?

    fun interface OutputPort {
        suspend fun characterArcNameVariantRenamed(response: ResponseModel)
    }

}