package com.soyle.stories.scene.charactersInScene.setDesire

import com.soyle.stories.usecase.scene.character.setDesire.SetCharacterDesireInScene

class SetCharacterDesireInSceneOutput(
    private val characterDesireInSceneChangedReceiver: CharacterDesireInSceneChangedReceiver
) : SetCharacterDesireInScene.OutputPort {

    override suspend fun receiveSetCharacterDesireInSceneResponse(response: SetCharacterDesireInScene.ResponseModel) {
        response.characterDesireInSceneChanged?.let {
            characterDesireInSceneChangedReceiver.receiveCharacterDesireInSceneChanged(it)
        }
    }
}