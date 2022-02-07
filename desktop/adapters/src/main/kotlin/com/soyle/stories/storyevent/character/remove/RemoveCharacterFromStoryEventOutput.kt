package com.soyle.stories.storyevent.character.remove

import com.soyle.stories.common.Receiver
import com.soyle.stories.domain.storyevent.character.changes.CharacterRemovedFromStoryEvent
import com.soyle.stories.usecase.storyevent.character.remove.RemoveCharacterFromStoryEvent

class RemoveCharacterFromStoryEventOutput(
    private val characterRemovedFromStoryEventReceiver: Receiver<CharacterRemovedFromStoryEvent>
) : RemoveCharacterFromStoryEvent.OutputPort {
    override suspend fun characterRemovedFromStoryEvent(characterRemoved: CharacterRemovedFromStoryEvent) {
        characterRemovedFromStoryEventReceiver.receiveEvent(characterRemoved)
    }
}