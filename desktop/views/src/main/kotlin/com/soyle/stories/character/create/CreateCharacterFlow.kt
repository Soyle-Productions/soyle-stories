package com.soyle.stories.character.create

import com.soyle.stories.usecase.character.buildNewCharacter.CreatedCharacter

interface CreateCharacterFlow {

    fun start(onCharacterCreated: (CreatedCharacter) -> Unit = {})

}