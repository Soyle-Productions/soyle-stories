package com.soyle.stories.usecase.character.nameVariant.remove

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterDoesNotHaveNameVariant
import com.soyle.stories.domain.character.CharacterException
import com.soyle.stories.domain.character.CharacterUpdate
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
        val character = characterRepository.getCharacterOrError(characterId.uuid)
        val update = character.withoutNameVariant(variant)
        when (update) {
            is CharacterUpdate.Updated -> {
                characterRepository.updateCharacter(update.character)
                output.receiveRemoveCharacterNameVariantResponse(RemoveCharacterNameVariant.ResponseModel(update.event))
                return null
            }
            is CharacterUpdate.WithoutChange -> {
                val failure = update.reason
                if (failure is Throwable) throw failure as Throwable
                return failure
            }
        }
    }
}