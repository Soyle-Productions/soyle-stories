package com.soyle.stories.usecase.storyevent.remove

import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.shared.potential.PotentialChanges

class PotentialChangesOfRemovingStoryEventFromProject(
    items: List<ImplicitCharacterRemovedFromScene>
) : List<ImplicitCharacterRemovedFromScene> by items, PotentialChanges<RemoveStoryEventFromProject>