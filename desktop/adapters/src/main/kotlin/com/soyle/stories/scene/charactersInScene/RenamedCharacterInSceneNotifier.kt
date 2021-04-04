package com.soyle.stories.scene.charactersInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.RenamedCharacterInScene

class RenamedCharacterInSceneNotifier : Notifier<RenamedCharacterInSceneReceiver>(), RenamedCharacterInSceneReceiver {

    override suspend fun receiveRenamedCharacterInScene(renamedCharacterInScene: RenamedCharacterInScene) {
        notifyAll { it.receiveRenamedCharacterInScene(renamedCharacterInScene) }
    }
}