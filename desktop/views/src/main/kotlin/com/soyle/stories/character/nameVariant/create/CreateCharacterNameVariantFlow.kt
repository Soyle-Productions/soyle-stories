package com.soyle.stories.character.nameVariant.create

import com.soyle.stories.domain.character.Character

interface CreateCharacterNameVariantFlow {

    fun start(characterId: Character.Id)

}