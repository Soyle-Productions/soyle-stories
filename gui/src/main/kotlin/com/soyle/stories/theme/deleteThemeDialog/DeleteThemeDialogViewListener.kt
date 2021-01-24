package com.soyle.stories.theme.deleteThemeDialog

interface DeleteThemeDialogViewListener {

    fun getValidState()
    fun deleteTheme(showAgain: Boolean)

}