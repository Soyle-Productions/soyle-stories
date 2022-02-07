package com.soyle.stories.character.renameCharacter

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.NonBlankString
import kotlinx.coroutines.Job

interface RenameCharacterController {

	fun renameCharacter(characterId: Character.Id, currentName: NonBlankString, newName: NonBlankString): Job

}