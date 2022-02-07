package com.soyle.stories.storyevent.character.add

import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.storyevent.character.involve.AvailableCharactersToInvolveInStoryEvent

fun interface SelectCharacterPrompt {
    suspend fun selectCharacter(availableCharacters: AvailableCharactersToInvolveInStoryEvent): Character.Id?
}