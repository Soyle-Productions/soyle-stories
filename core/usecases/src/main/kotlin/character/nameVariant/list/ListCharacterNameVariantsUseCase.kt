package com.soyle.stories.usecase.character.nameVariant.list

import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.CharacterRepository
import java.util.*

class ListCharacterNameVariantsUseCase(private val characterRepository: CharacterRepository) : ListCharacterNameVariants {

    override suspend fun invoke(characterId: Character.Id, output: ListCharacterNameVariants.OutputPort) {
        val character = characterRepository.getCharacterOrError(characterId.uuid)
        output.receiveCharacterNameVariants(ListCharacterNameVariants.ResponseModel(character.otherNames.map { it.value }))
    }
}