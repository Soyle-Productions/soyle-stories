package com.soyle.stories.usecase.character.remove

import com.soyle.stories.usecase.scene.character.effects.CharacterInSceneEffect
import com.soyle.stories.usecase.shared.potential.PotentialChanges

class PotentialChangesOfRemovingCharacterFromStory(
    items: List<CharacterInSceneEffect>
) : PotentialChanges<RemoveCharacterFromStory>,
    List<CharacterInSceneEffect> by items