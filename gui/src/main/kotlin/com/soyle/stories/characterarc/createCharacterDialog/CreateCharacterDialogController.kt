package com.soyle.stories.characterarc.createCharacterDialog

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterController

class CreateCharacterDialogController(
    private val buildNewCharacterController: BuildNewCharacterController
) : CreateCharacterDialogViewListener {

    override fun createCharacter(name: String) {
        buildNewCharacterController.createCharacter(name) { throw it }
    }

    override fun createCharacterAndIncludeInTheme(name: String, includeInTheme: String) {
        buildNewCharacterController.createCharacterAndIncludeInTheme(name, includeInTheme) { throw it }
    }

    override fun createCharacterAsMajorCharacter(name: String, includeInTheme: String) {
        buildNewCharacterController.createCharacterAsMajorCharacter(name, includeInTheme) { throw it }
    }

    override fun createCharacterForUseAsOpponent(name: String, includeInTheme: String, opponentForCharacter: String) {
        buildNewCharacterController.createCharacterForUseAsOpponent(name, includeInTheme, opponentForCharacter) { throw it }
    }
}