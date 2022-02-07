package com.soyle.stories.storyevent.coverage.uncover

import com.soyle.stories.usecase.storyevent.coverage.uncover.PotentialChangesFromUncoveringStoryEvent

fun interface UncoverStoryEventRamificationsReport {
    suspend fun showRamifications(potentialChanges: PotentialChangesFromUncoveringStoryEvent)
}