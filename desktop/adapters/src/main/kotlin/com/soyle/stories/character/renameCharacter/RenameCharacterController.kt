package com.soyle.stories.character.renameCharacter

import com.soyle.stories.domain.validation.NonBlankString
import kotlinx.coroutines.Job

interface RenameCharacterController {

	fun renameCharacter(characterId: String, newName: NonBlankString): Job

}