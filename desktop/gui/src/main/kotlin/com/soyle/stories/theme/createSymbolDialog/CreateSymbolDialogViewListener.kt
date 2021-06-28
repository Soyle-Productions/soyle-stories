package com.soyle.stories.theme.createSymbolDialog

import com.soyle.stories.domain.validation.NonBlankString

interface CreateSymbolDialogViewListener {

    fun getValidState()
    fun createThemeAndSymbol(themeName: NonBlankString, symbolName: NonBlankString)
    fun createSymbol(themeId: String, name: NonBlankString)
    fun linkToOpposition(symbolId: String, oppositionId: String)

}