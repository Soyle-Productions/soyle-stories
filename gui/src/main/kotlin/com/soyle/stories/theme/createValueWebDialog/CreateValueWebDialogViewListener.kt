package com.soyle.stories.theme.createValueWebDialog

interface CreateValueWebDialogViewListener {

    fun getValidState()
    fun createValueWeb(themeId: String, name: String)
    fun createValueWebAndLinkCharacter(themeId: String, name: String, characterId: String)

}