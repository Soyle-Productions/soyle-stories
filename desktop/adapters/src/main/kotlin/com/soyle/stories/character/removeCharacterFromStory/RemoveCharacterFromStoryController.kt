package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.domain.character.Character
import kotlinx.coroutines.Job

interface RemoveCharacterFromStoryController {

    fun removeCharacter(characterId: Character.Id, prompt: ConfirmationPrompt, report: RamificationsReport): Job

}