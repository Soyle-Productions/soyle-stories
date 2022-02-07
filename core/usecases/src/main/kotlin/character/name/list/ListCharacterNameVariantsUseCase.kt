package com.soyle.stories.usecase.character.name.list

import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.character.CharacterRepository

class ListCharacterNameVariantsUseCase(private val characterRepository: CharacterRepository) : ListCharacterNameVariants {

    override suspend fun invoke(characterId: Character.Id, output: ListCharacterNameVariants.OutputPort) {
        val character = characterRepository.getCharacterOrError(characterId.uuid)
        output.receiveCharacterNameVariants(ListCharacterNameVariants.ResponseModel(character.names.secondaryNames.map { it.value }))
    }
}