package com.soyle.stories.storyevent.addCharacterToStoryEvent

import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.IncludedCharacterInStoryEvent

interface IncludedCharacterInStoryEventReceiver {

    suspend fun receiveIncludedCharacterInStoryEvent(includedCharacterInStoryEvent: IncludedCharacterInStoryEvent)

}