package com.soyle.stories.scene.charactersInScene.setDesire

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.character.events.CharacterDesireInSceneChanged

class CharacterDesireInSceneChangedNotifier : Notifier<CharacterDesireInSceneChangedReceiver>(), CharacterDesireInSceneChangedReceiver {

    override suspend fun receiveCharacterDesireInSceneChanged(event: CharacterDesireInSceneChanged) {
        notifyAll { it.receiveCharacterDesireInSceneChanged(event) }
    }
}