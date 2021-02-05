package com.soyle.stories.character.renameCharacter

import com.soyle.stories.common.NonBlankString

interface RenameCharacterController {

	fun renameCharacter(characterId: String, newName: NonBlankString)

}