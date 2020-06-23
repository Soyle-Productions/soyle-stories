package com.soyle.stories.theme.themeList

interface ThemeListViewListener {

    fun getValidState()
    fun openValueWeb(themeId: String)
    fun openCharacterComparison(themeId: String)
    fun renameTheme(themeId: String, newName: String)

}