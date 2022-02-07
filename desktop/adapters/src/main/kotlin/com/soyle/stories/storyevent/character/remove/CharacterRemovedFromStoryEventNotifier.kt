package com.soyle.stories.storyevent.character.remove

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.Receiver
import com.soyle.stories.domain.storyevent.character.changes.CharacterRemovedFromStoryEvent

class CharacterRemovedFromStoryEventNotifier : Notifier<Receiver<CharacterRemovedFromStoryEvent>>(),
    Receiver<CharacterRemovedFromStoryEvent> {

    override suspend fun receiveEvent(event: CharacterRemovedFromStoryEvent) {
        notifyAll { it.receiveEvent(event) }
    }
}