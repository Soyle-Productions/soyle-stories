package com.soyle.stories.usecase.character.nameVariant.remove

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterException
import com.soyle.stories.domain.character.events.CharacterNameVariantRemoved
import com.soyle.stories.domain.validation.NonBlankString

interface RemoveCharacterNameVariant {

    class ResponseModel(val characterNameVariantRemoved: CharacterNameVariantRemoved)

    suspend operator fun invoke(characterId: Character.Id, variant: NonBlankString, output: OutputPort): CharacterException?

    fun interface OutputPort {
        suspend fun receiveRemoveCharacterNameVariantResponse(response: ResponseModel)
    }

}