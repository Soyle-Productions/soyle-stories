package com.soyle.stories.scene.charactersInScene.includeCharacterInScene

import com.soyle.stories.common.Receiver
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.usecase.scene.character.include.IncludeCharacterInScene

class IncludeCharacterInSceneOutput(
    private val characterIncludedInSceneReceiver: Receiver<CharacterIncludedInScene>
) : IncludeCharacterInScene.OutputPort {

    override suspend fun characterIncludedInScene(response: IncludeCharacterInScene.ResponseModel) {
        characterIncludedInSceneReceiver.receiveEvent(response.characterIncludedInScene)
    }
}