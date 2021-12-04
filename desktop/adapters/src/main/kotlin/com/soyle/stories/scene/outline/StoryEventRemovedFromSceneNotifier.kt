package com.soyle.stories.scene.outline

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene

class StoryEventRemovedFromSceneNotifier : Notifier<StoryEventRemovedFromSceneReceiver>(), StoryEventRemovedFromSceneReceiver {
    override suspend fun receiveStoryEventRemovedFromScene(event: StoryEventRemovedFromScene) {
        notifyAll { it.receiveStoryEventRemovedFromScene(event) }
    }
}