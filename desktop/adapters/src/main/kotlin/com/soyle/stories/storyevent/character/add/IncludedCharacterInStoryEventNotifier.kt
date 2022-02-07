package com.soyle.stories.storyevent.character.add

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.storyevent.character.changes.CharacterInvolvedInStoryEvent
import com.soyle.stories.usecase.storyevent.character.involve.IncludedCharacterInStoryEvent

class IncludedCharacterInStoryEventNotifier : Notifier<IncludedCharacterInStoryEventReceiver>(),
    IncludedCharacterInStoryEventReceiver {

    override suspend fun receiveCharacterInvolvedInStoryEvent(event: CharacterInvolvedInStoryEvent) {
        notifyAll { it.receiveCharacterInvolvedInStoryEvent(event) }
    }
}