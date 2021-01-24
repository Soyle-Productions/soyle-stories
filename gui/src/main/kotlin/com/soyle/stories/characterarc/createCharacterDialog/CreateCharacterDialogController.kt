package com.soyle.stories.characterarc.createCharacterDialog

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterController
import com.soyle.stories.common.NonBlankString

class CreateCharacterDialogController(
    private val buildNewCharacterController: BuildNewCharacterController
) : CreateCharacterDialogViewListener {

    override fun createCharacter(name: NonBlankString) {
        buildNewCharacterController.createCharacter(name) { throw it }
    }

    override fun createCharacterAndIncludeInTheme(name: NonBlankString, includeInTheme: String) {
        buildNewCharacterController.createCharacterAndIncludeInTheme(name, includeInTheme) { throw it }
    }

    override fun createCharacterAsMajorCharacter(name: NonBlankString, includeInTheme: String) {
        buildNewCharacterController.createCharacterAsMajorCharacter(name, includeInTheme) { throw it }
    }

    override fun createCharacterForUseAsOpponent(name: NonBlankString, includeInTheme: String, opponentForCharacter: String) {
        buildNewCharacterController.createCharacterForUseAsOpponent(name, includeInTheme, opponentForCharacter) { throw it }
    }
}