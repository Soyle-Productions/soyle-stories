package com.soyle.stories.character.nameVariant.addNameVariant

import com.soyle.stories.usecase.character.nameVariant.create.AddCharacterNameVariant

class AddCharacterNameVariantOutput(
    private val characterNameVariantAddedReceiver: CharacterNameVariantAddedReceiver
) : AddCharacterNameVariant.OutputPort {

    override suspend fun addedCharacterNameVariant(response: AddCharacterNameVariant.ResponseModel) {
        characterNameVariantAddedReceiver.receiveCharacterNameVariantAdded(response.characterNameVariantAdded)
    }
}