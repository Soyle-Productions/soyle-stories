package com.soyle.stories.character.nameVariant.rename

import com.soyle.stories.usecase.character.nameVariant.rename.RenameCharacterNameVariant

class RenameCharacterNameVariantOutput(
    private val characterNameVariantRenamedReceiver: CharacterNameVariantRenamedReceiver
) : RenameCharacterNameVariant.OutputPort {

    override suspend fun characterArcNameVariantRenamed(response: RenameCharacterNameVariant.ResponseModel) {
        characterNameVariantRenamedReceiver.receiveCharacterNameVariantRenamed(response.characterNameVariantRenamed)
    }
}