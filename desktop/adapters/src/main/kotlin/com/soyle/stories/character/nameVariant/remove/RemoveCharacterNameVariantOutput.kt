package com.soyle.stories.character.nameVariant.remove

import com.soyle.stories.usecase.character.name.remove.RemoveCharacterNameVariant

class RemoveCharacterNameVariantOutput(
    private val receiver: CharacterNameVariantRemovedReceiver
) : RemoveCharacterNameVariant.OutputPort {

    override suspend fun receiveRemoveCharacterNameVariantResponse(response: RemoveCharacterNameVariant.ResponseModel) {
        receiver.receiveCharacterNameVariantRemoved(response.characterNameRemoved)
    }
}