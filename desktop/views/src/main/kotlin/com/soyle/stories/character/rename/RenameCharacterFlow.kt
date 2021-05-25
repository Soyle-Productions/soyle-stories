package com.soyle.stories.character.rename

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.NonBlankString

interface RenameCharacterFlow {

    fun start(characterId: Character.Id, currentName: String? = null)

}