package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.domain.character.Character
import kotlinx.coroutines.Job

interface RemoveCharacterFromStoryController {

    fun requestRemoveCharacter(characterId: String)
    fun confirmRemoveCharacter(characterId: Character.Id): Job

}