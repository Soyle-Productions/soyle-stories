package com.soyle.stories.usecase.character.remove

import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.shared.potential.GetPotentialChanges

interface GetPotentialChangesOfRemovingCharacterFromStory : GetPotentialChanges<RemoveCharacterFromStory> {

    suspend operator fun invoke(characterId: Character.Id, output: OutputPort)

    fun interface OutputPort {
        suspend fun receivePotentialChanges(response: PotentialChangesOfRemovingCharacterFromStory)
    }

}