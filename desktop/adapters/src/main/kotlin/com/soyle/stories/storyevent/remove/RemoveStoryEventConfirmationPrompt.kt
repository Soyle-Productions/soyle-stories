package com.soyle.stories.storyevent.remove

import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.common.Confirmation
import com.soyle.stories.domain.storyevent.StoryEvent

fun interface RemoveStoryEventConfirmationPrompt {
    suspend fun confirmRemoveStoryEventsFromProject(storyEvents: List<StoryEvent>): Confirmation<ConfirmationPrompt.Response>
}