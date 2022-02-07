package com.soyle.stories.usecase.character.name.create

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.name.events.CharacterNameAdded
import com.soyle.stories.domain.validation.NonBlankString

interface AddCharacterNameVariant {
    suspend operator fun invoke(characterId: Character.Id, variant: NonBlankString, output: OutputPort)

    class ResponseModel(
        val characterNameAdded: CharacterNameAdded
    )

    fun interface OutputPort {
        suspend fun addedCharacterNameVariant(response: ResponseModel)
    }
}