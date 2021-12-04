package com.soyle.stories.storyevent.coverage

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene

class StoryEventCoveredBySceneNotifier : Notifier<StoryEventCoveredBySceneReceiver>(), StoryEventCoveredBySceneReceiver {
    override suspend fun receiveStoryEventCoveredByScene(event: StoryEventCoveredByScene) {
        notifyAll { it.receiveStoryEventCoveredByScene(event) }
    }
}