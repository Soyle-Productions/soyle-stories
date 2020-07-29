package com.soyle.stories.characterarc.createCharacterDialog

interface CreateCharacterDialogViewListener {

    fun createCharacter(name: String)
    fun createCharacterAndIncludeInTheme(name: String, includeInTheme: String)
    fun createCharacterForUseAsOpponent(name: String, includeInTheme: String, opponentForCharacter: String)
    fun createCharacterAsMajorCharacter(name: String, includeInTheme: String)

}