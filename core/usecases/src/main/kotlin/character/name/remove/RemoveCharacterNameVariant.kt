package com.soyle.stories.usecase.character.name.remove

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.exceptions.CharacterException
import com.soyle.stories.domain.character.name.events.CharacterNameRemoved
import com.soyle.stories.domain.validation.NonBlankString

interface RemoveCharacterNameVariant {

    class ResponseModel(val characterNameRemoved: CharacterNameRemoved)

    suspend operator fun invoke(characterId: Character.Id, variant: NonBlankString, output: OutputPort): CharacterException?

    fun interface OutputPort {
        suspend fun receiveRemoveCharacterNameVariantResponse(response: ResponseModel)
    }

}