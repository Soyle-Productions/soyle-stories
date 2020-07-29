package com.soyle.stories.character.buildNewCharacter

interface BuildNewCharacterController {

    fun buildNewCharacter(name: String, includeInTheme: String? = null, useAsOpponentForCharacter: String? = null, onError: (Throwable) -> Unit)

}