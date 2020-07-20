package com.soyle.stories.characterarc.createCharacterDialog

interface CreateCharacterDialogViewListener {

    fun createCharacter(name: String, includeInTheme: String? = null)

}