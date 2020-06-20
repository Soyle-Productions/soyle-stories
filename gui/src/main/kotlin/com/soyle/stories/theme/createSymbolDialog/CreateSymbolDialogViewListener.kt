package com.soyle.stories.theme.createSymbolDialog

interface CreateSymbolDialogViewListener {

    fun getValidState()
    fun createSymbol(themeId: String, name: String)

}