package com.soyle.stories.controllers.character.buildNewCharacter

interface BuildNewCharacterController {

    fun createCharacter(name: String, onError: (Throwable) -> Unit)
    fun createCharacterAndIncludeInTheme(name: String, includeInTheme: String, onError: (Throwable) -> Unit)
    fun createCharacterForUseAsOpponent(name: String, includeInTheme: String, opponentForCharacter: String, onError: (Throwable) -> Unit)
    fun createCharacterAsMajorCharacter(name: String, includeInTheme: String, onError: (Throwable) -> Unit)

}