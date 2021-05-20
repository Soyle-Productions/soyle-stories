package com.soyle.stories.usecase.character.nameVariant.rename

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterDoesNotHaveNameVariant
import com.soyle.stories.domain.character.CharacterException
import com.soyle.stories.domain.character.CharacterUpdate
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.CharacterRepository
import java.util.*

class RenameCharacterNameVariantUseCase(
    private val characterRepository: CharacterRepository
) : RenameCharacterNameVariant {

    override suspend fun invoke(request: RenameCharacterNameVariant.RequestModel, output: RenameCharacterNameVariant.OutputPort): CharacterException? {
        val character = characterRepository.getCharacterOrError(request.characterId.uuid)
        val update = character.withNameVariantModified(request.currentVariant, request.newVariant)
        when (update) {
            is CharacterUpdate.Updated -> {
                characterRepository.updateCharacter(update.character)
                output.characterArcNameVariantRenamed(RenameCharacterNameVariant.ResponseModel(update.event))
                return null
            }
            is CharacterUpdate.WithoutChange -> {
                val failure = update.reason
                if (failure is CharacterDoesNotHaveNameVariant) throw failure as Exception
                return failure
            }
        }
    }

}