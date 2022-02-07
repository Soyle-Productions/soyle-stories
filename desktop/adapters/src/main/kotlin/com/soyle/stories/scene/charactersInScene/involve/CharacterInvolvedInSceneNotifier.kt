package com.soyle.stories.scene.charactersInScene.involve

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.scene.character.involve.CharacterInvolvedInScene

class CharacterInvolvedInSceneNotifier : Notifier<CharacterInvolvedInSceneReceiver>(), CharacterInvolvedInSceneReceiver {

    val activeListeners get() = listeners

    override suspend fun receiveCharacterInvolvedInScene(event: CharacterInvolvedInScene) {
        notifyAll { it.receiveCharacterInvolvedInScene(event) }
    }
}