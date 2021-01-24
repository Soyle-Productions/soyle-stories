package com.soyle.stories.theme.createSymbolDialog

interface CreateSymbolDialogViewListener {

    fun getValidState()
    fun createThemeAndSymbol(themeName: String, symbolName: String)
    fun createSymbol(themeId: String, name: String)
    fun linkToOpposition(symbolId: String, oppositionId: String)

}