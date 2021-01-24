package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.entities.Character

interface RemoveCharacterFromStoryController {

    fun requestRemoveCharacter(characterId: String)
    fun confirmRemoveCharacter(characterId: Character.Id)

}