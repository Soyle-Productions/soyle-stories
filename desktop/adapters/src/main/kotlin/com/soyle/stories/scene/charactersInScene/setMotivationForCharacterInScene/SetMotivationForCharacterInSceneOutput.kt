package com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.Receiver
import com.soyle.stories.domain.scene.character.events.CharacterGainedMotivationInScene
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.domain.scene.character.events.CharacterMotivationInSceneCleared
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInScene

class SetMotivationForCharacterInSceneOutput(
    private val characterGainedMotivationInSceneReceiver: Receiver<CharacterGainedMotivationInScene>,
    private val characterMotivationInSceneClearedReceiver: Receiver<CharacterMotivationInSceneCleared>,
    private val characterIncludedInSceneReceiver: Receiver<CharacterIncludedInScene>
) : SetMotivationForCharacterInScene.OutputPort {

    override suspend fun motivationSetForCharacterInScene(response: SetMotivationForCharacterInScene.ResponseModel) {
        response.characterIncludedInScene?.let { characterIncludedInSceneReceiver.receiveEvent(it) }
        when (val change = response.characterMotivationInSceneChanged) {
            is CharacterGainedMotivationInScene -> characterGainedMotivationInSceneReceiver.receiveEvent(change)
            is CharacterMotivationInSceneCleared -> characterMotivationInSceneClearedReceiver.receiveEvent(change)
        }
    }

}