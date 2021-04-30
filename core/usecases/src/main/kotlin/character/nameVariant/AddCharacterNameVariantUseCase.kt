package com.soyle.stories.usecase.character.nameVariant

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterUpdate
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.CharacterRepository
import java.util.*

class AddCharacterNameVariantUseCase(
    private val characterRepository: CharacterRepository
) : AddCharacterNameVariant {

    override suspend fun invoke(characterId: Character.Id, variant: NonBlankString, output: AddCharacterNameVariant.OutputPort) {
        val character = characterRepository.getCharacterOrError(characterId.uuid)
        val characterUpdate = character.withNameVariant(variant)
        if (characterUpdate is CharacterUpdate.Updated) {
            characterRepository.updateCharacter(characterUpdate.character)
            output.addedCharacterNameVariant(AddCharacterNameVariant.ResponseModel(characterUpdate.event))
        }
    }
}