package com.soyle.stories.storyevent.character.add

import com.soyle.stories.domain.storyevent.character.changes.CharacterInvolvedInStoryEvent

interface IncludedCharacterInStoryEventReceiver {

    suspend fun receiveCharacterInvolvedInStoryEvent(event: CharacterInvolvedInStoryEvent)

}