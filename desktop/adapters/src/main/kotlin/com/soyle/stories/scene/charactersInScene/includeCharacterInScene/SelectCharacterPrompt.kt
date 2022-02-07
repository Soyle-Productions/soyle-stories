package com.soyle.stories.scene.charactersInScene.includeCharacterInScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.AvailableCharactersToAddToScene

interface SelectCharacterPrompt {

    suspend fun selectCharacter(availableCharacters: AvailableCharactersToAddToScene): CharacterSelection?

    suspend fun done()

    sealed class CharacterSelection {
        object CreateNew : CharacterSelection()
        class Selected(val id: Character.Id) : CharacterSelection()
    }

}