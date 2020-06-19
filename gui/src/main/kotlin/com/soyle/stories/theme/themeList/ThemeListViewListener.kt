package com.soyle.stories.theme.themeList

interface ThemeListViewListener {

    fun getValidState()
    fun openCharacterComparison(themeId: String)
    fun renameTheme(themeId: String, newName: String)

}