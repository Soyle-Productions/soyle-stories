package com.soyle.stories.characterarc.createCharacterDialog

import com.soyle.stories.project.ProjectScope
import com.soyle.stories.usecase.character.buildNewCharacter.CreatedCharacter

interface CreateCharacterDialog {

    fun create(onCharacterCreated: (CreatedCharacter) -> Unit = {})

}