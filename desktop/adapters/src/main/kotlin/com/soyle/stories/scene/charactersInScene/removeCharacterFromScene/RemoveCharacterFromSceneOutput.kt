package com.soyle.stories.scene.charactersInScene.removeCharacterFromScene

import com.soyle.stories.common.Receiver
import com.soyle.stories.domain.scene.character.events.CharacterRemovedFromScene
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromScene

class RemoveCharacterFromSceneOutput(
    private val removedCharacterFromSceneReceiver: Receiver<CharacterRemovedFromScene>
) : RemoveCharacterFromScene.OutputPort {

    override suspend fun characterRemovedFromScene(response: RemoveCharacterFromScene.ResponseModel) {
        removedCharacterFromSceneReceiver.receiveEvent(response.characterRemoved)
    }
}