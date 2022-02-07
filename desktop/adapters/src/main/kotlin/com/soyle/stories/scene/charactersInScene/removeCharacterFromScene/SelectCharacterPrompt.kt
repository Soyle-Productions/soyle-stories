package com.soyle.stories.scene.charactersInScene.removeCharacterFromScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.scene.character.list.CharactersInScene

interface SelectCharacterPrompt {
    suspend fun selectCharacter(charactersInScene: CharactersInScene): Character.Id?
}