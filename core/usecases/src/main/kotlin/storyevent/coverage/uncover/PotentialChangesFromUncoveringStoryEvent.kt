package com.soyle.stories.usecase.storyevent.coverage.uncover

import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.shared.potential.PotentialChanges

class PotentialChangesFromUncoveringStoryEvent(
    items: List<ImplicitCharacterRemovedFromScene>
) : List<ImplicitCharacterRemovedFromScene> by items,
    PotentialChanges<UncoverStoryEventFromScene>