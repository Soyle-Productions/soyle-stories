package com.soyle.stories.domain.storyevent.character

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.StoryEventUpdate
import com.soyle.stories.domain.storyevent.character.changes.CharacterInvolvedWithStoryEventRenamed
import com.soyle.stories.domain.storyevent.character.exceptions.involvedCharacterAlreadyHasName

class InvolvedCharacterOperations internal constructor(
    private val storyEvent: StoryEvent,
    private val involvedCharacter: InvolvedCharacter
) {

    internal fun renamed(name: String): StoryEventUpdate<CharacterInvolvedWithStoryEventRenamed> {
        if (involvedCharacter.name == name)
            return storyEvent.noUpdate(involvedCharacterAlreadyHasName(storyEvent.id, involvedCharacter.id, name))

        return storyEvent.withChangeApplied(CharacterInvolvedWithStoryEventRenamed(storyEvent.id, involvedCharacter.id, name))
    }

}