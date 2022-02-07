package com.soyle.stories.storyevent.remove

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.remove.PotentialChangesOfRemovingStoryEventFromProject

fun interface RemoveStoryEventFromProjectRamificationsReport {
    suspend fun showPotentialChanges(potentialChanges: PotentialChangesOfRemovingStoryEventFromProject)
}