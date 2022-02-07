package com.soyle.stories.usecase.storyevent.character.remove

import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.shared.potential.PotentialChanges

class PotentialChangesOfRemovingCharacterFromStoryEvent(
    items: List<ImplicitCharacterRemovedFromScene>
) : PotentialChanges<RemoveCharacterFromStoryEvent>,
        List<ImplicitCharacterRemovedFromScene> by items