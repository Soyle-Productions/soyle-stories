package com.soyle.stories.usecase.character.name.remove

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterDoesNotHaveNameVariant
import com.soyle.stories.domain.character.CharacterUpdate
import com.soyle.stories.domain.character.exceptions.CharacterException
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.CharacterRepository

class RemoveCharacterNameVariantUseCase(
    private val characterRepository: CharacterRepository
) : RemoveCharacterNameVariant {

    override suspend fun invoke(
        characterId: Character.Id,
        variant: NonBlankString,
        output: RemoveCharacterNameVariant.OutputPort
    ): CharacterException? {
        val character = characterRepository.getCharacterById(characterId)
            ?: return CharacterDoesNotExist(characterId)
        val nameOps = character.withName(variant.value)
            ?: return CharacterDoesNotHaveNameVariant(character.id, variant.value)
        return when (val update = nameOps.removed()) {
            is CharacterUpdate.Updated -> {
                characterRepository.updateCharacter(update.character)
                output.receiveRemoveCharacterNameVariantResponse(RemoveCharacterNameVariant.ResponseModel(update.event))
                null
            }
            is CharacterUpdate.WithoutChange -> {
                return update.reason
            }
        }
    }
}