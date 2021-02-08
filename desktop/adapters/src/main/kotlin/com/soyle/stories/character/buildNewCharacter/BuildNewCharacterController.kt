package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.domain.validation.NonBlankString

interface BuildNewCharacterController {

    fun createCharacter(name: NonBlankString, onError: (Throwable) -> Unit)
    fun createCharacterAndIncludeInTheme(name: NonBlankString, includeInTheme: String, onError: (Throwable) -> Unit)
    fun createCharacterForUseAsOpponent(name: NonBlankString, includeInTheme: String, opponentForCharacter: String, onError: (Throwable) -> Unit)
    fun createCharacterAsMajorCharacter(name: NonBlankString, includeInTheme: String, onError: (Throwable) -> Unit)

}