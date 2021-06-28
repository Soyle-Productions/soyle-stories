package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.usecase.character.createPerspectiveCharacter.CreatePerspectiveCharacter
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeReceiver

class CreatePerspectiveCharacterOutput(
    private val createdCharacterReceiver: CreatedCharacterReceiver,
    private val characterIncludedInThemeReceiver: CharacterIncludedInThemeReceiver
) : CreatePerspectiveCharacter.OutputPort {
    override suspend fun createdPerspectiveCharacter(response: CreatePerspectiveCharacter.ResponseModel) {
        createdCharacterReceiver.receiveCreatedCharacter(response.createdCharacter)
        characterIncludedInThemeReceiver.receiveCharacterIncludedInTheme(response.characterIncludedInTheme)
    }
}