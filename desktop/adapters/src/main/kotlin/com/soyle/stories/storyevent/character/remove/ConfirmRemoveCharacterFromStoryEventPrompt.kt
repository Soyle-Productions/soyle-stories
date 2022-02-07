package com.soyle.stories.storyevent.character.remove

import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.common.Confirmation
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent

fun interface ConfirmRemoveCharacterFromStoryEventPrompt {
    suspend fun confirmRemoveCharacter(storyEvent: StoryEvent, character: Character): Confirmation<ConfirmationPrompt.Response>
}