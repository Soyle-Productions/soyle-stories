package com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.Receiver
import com.soyle.stories.domain.scene.character.events.CharacterMotivationInSceneCleared

class CharacterMotivationInSceneClearedNotifier : Notifier<Receiver<CharacterMotivationInSceneCleared>>(),
    Receiver<CharacterMotivationInSceneCleared> {
    override suspend fun receiveEvent(event: CharacterMotivationInSceneCleared) {
        notifyAll { it.receiveEvent(event) }
    }
}