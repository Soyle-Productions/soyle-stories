package com.soyle.stories.theme.themeList

interface ThemeListViewListener {

    fun getValidState()
    fun openValueWeb(themeId: String)
    fun openCharacterComparison(themeId: String)
    fun openCentralConflict(themeId: String)
    fun openMoralArgument(themeId: String)
    fun renameTheme(themeId: String, newName: String)
    fun renameSymbol(symbolId: String, newName: String)

}