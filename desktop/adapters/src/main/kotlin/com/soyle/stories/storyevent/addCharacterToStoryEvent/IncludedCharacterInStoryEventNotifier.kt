package com.soyle.stories.storyevent.addCharacterToStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.IncludedCharacterInStoryEvent

class IncludedCharacterInStoryEventNotifier : Notifier<IncludedCharacterInStoryEventReceiver>(), IncludedCharacterInStoryEventReceiver {

    override suspend fun receiveIncludedCharacterInStoryEvent(includedCharacterInStoryEvent: IncludedCharacterInStoryEvent) {
        notifyAll { it.receiveIncludedCharacterInStoryEvent(includedCharacterInStoryEvent) }
    }
}