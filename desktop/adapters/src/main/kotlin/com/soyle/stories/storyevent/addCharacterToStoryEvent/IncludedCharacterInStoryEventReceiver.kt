package com.soyle.stories.storyevent.addCharacterToStoryEvent

import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.IncludedCharacterInStoryEvent

interface IncludedCharacterInStoryEventReceiver {

    suspend fun receiveIncludedCharacterInStoryEvent(includedCharacterInStoryEvent: IncludedCharacterInStoryEvent)

}