package com.soyle.stories.scene.charactersInScene.includeCharacterInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.usecase.scene.character.include.IncludeCharacterInScene

class CharacterIncludedInSceneNotifier : Notifier<CharacterIncludedInSceneReceiver>(), CharacterIncludedInSceneReceiver {

    override suspend fun receiveEvent(event: CharacterIncludedInScene) {
        notifyAll { it.receiveEvent(event) }
    }
}