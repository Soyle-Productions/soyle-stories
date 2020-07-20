package com.soyle.stories.character.buildNewCharacter

interface BuildNewCharacterController {

    fun buildNewCharacter(name: String, includeInTheme: String?, onError: (Throwable) -> Unit)

}