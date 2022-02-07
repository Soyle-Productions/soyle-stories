package com.soyle.stories.storyevent.character.remove

import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.common.Confirmation
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.character.remove.PotentialChangesOfRemovingCharacterFromStoryEvent

fun interface RemoveCharacterFromStoryEventRamificationsReport {
    suspend fun showRamifications(potentialChanges: PotentialChangesOfRemovingCharacterFromStoryEvent)
}