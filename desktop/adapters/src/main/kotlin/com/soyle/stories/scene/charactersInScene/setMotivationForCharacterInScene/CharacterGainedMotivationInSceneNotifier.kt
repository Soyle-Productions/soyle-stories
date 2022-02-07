package com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.Receiver
import com.soyle.stories.domain.scene.character.events.CharacterGainedMotivationInScene

class CharacterGainedMotivationInSceneNotifier : Notifier<Receiver<CharacterGainedMotivationInScene>>(), Receiver<CharacterGainedMotivationInScene> {
    override suspend fun receiveEvent(event: CharacterGainedMotivationInScene) {
        notifyAll { it.receiveEvent(event) }
    }
}