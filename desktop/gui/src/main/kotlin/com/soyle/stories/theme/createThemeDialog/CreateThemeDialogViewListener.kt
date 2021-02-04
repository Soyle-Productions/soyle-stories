package com.soyle.stories.theme.createThemeDialog

interface CreateThemeDialogViewListener {

    fun getValidState()
    fun createTheme(name: String)

}