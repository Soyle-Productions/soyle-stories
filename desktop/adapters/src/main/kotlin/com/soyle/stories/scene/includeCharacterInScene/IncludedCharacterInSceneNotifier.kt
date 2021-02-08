package com.soyle.stories.scene.includeCharacterInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene

class IncludedCharacterInSceneNotifier : Notifier<IncludedCharacterInSceneReceiver>(), IncludedCharacterInSceneReceiver {

    override suspend fun receiveIncludedCharacterInScene(includedCharacterInScene: IncludedCharacterInScene) {
        notifyAll { it.receiveIncludedCharacterInScene(includedCharacterInScene) }
    }
}