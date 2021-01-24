package com.soyle.stories.theme.deleteSymbolDialog

interface DeleteSymbolDialogViewListener {

    fun getValidState()
    fun deleteSymbol(showAgain: Boolean)

}