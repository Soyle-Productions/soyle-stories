package com.soyle.stories.storyevent.coverage.uncover

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene

class StoryEventUncoveredBySceneNotifier : Notifier<StoryEventUncoveredBySceneReceiver>(),
    StoryEventUncoveredBySceneReceiver {
    override suspend fun receiveStoryEventUncoveredByScene(event: StoryEventUncoveredFromScene) {
        notifyAll { it.receiveStoryEventUncoveredByScene(event) }
    }
}