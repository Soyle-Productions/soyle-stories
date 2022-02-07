package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.usecase.character.remove.PotentialChangesOfRemovingCharacterFromStory
import com.soyle.stories.usecase.shared.potential.PotentialChanges

fun interface RamificationsReport {
    suspend fun showRamifications(potentialChanges: PotentialChangesOfRemovingCharacterFromStory): Unit?
}