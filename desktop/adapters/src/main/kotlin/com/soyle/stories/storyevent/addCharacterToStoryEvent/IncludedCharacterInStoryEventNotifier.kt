package com.soyle.stories.storyevent.addCharacterToStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.IncludedCharacterInStoryEvent

class IncludedCharacterInStoryEventNotifier : Notifier<IncludedCharacterInStoryEventReceiver>(), IncludedCharacterInStoryEventReceiver {

    override suspend fun receiveIncludedCharacterInStoryEvent(includedCharacterInStoryEvent: IncludedCharacterInStoryEvent) {
        notifyAll { it.receiveIncludedCharacterInStoryEvent(includedCharacterInStoryEvent) }
    }
}