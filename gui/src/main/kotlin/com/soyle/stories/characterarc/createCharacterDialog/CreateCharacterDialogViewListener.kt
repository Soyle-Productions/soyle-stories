package com.soyle.stories.characterarc.createCharacterDialog

import com.soyle.stories.common.NonBlankString

interface CreateCharacterDialogViewListener {

    fun createCharacter(name: NonBlankString)
    fun createCharacterAndIncludeInTheme(name: NonBlankString, includeInTheme: String)
    fun createCharacterForUseAsOpponent(name: NonBlankString, includeInTheme: String, opponentForCharacter: String)
    fun createCharacterAsMajorCharacter(name: NonBlankString, includeInTheme: String)

}