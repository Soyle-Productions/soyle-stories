package com.soyle.stories.character.buildNewCharacter

interface BuildNewCharacterController {

    fun buildNewCharacter(name: String, onError: (Throwable) -> Unit)

}