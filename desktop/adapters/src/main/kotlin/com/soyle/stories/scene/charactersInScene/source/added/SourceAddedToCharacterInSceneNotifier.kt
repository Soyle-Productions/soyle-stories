package com.soyle.stories.scene.charactersInScene.source.added

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.scene.character.involve.SourceAddedToCharacterInScene

class SourceAddedToCharacterInSceneNotifier : Notifier<SourceAddedToCharacterInSceneReceiver>(), SourceAddedToCharacterInSceneReceiver {
    val activeListeners get() = listeners

    override suspend fun receiverSourceAddedToCharacterInScene(event: SourceAddedToCharacterInScene) {
        notifyAll { it.receiverSourceAddedToCharacterInScene(event) }
    }
}