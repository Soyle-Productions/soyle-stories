package com.soyle.stories.scene.outline

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.StoryEventAddedToScene

class StoryEventAddedToSceneNotifier : Notifier<StoryEventAddedToSceneReceiver>(), StoryEventAddedToSceneReceiver {
    override suspend fun receiveStoryEventAddedToScene(event: StoryEventAddedToScene) {
        notifyAll { it.receiveStoryEventAddedToScene(event) }
    }
}