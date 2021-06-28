package com.soyle.stories.character.nameVariant.rename

import com.soyle.stories.domain.character.Character

interface RenameCharacterNameVariantFlow {

    fun start(characterId: Character.Id)

}