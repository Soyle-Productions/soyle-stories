package com.soyle.stories.theme.addSymbolDialog

interface AddSymbolDialogViewListener {

    fun getValidState()

    fun selectCharacter(characterId: String)
    fun selectLocation(locationId: String)
    fun selectSymbol(symbolId: String)

}