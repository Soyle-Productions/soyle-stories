package com.soyle.stories.characterarc.createCharacterDialog

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterController

class CreateCharacterDialogController(
    private val buildNewCharacterController: BuildNewCharacterController
) : CreateCharacterDialogViewListener {

    override fun createCharacter(name: String, includeInTheme: String?) {
        buildNewCharacterController.buildNewCharacter(name, includeInTheme) { throw it }
    }
}