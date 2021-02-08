package com.soyle.stories.theme.themeList

import com.soyle.stories.domain.validation.NonBlankString

interface ThemeListViewListener {

    fun getValidState()
    fun openValueWeb(themeId: String)
    fun openCharacterComparison(themeId: String)
    fun openCentralConflict(themeId: String)
    fun openMoralArgument(themeId: String)
    fun renameTheme(themeId: String, newName: NonBlankString)
    fun renameSymbol(symbolId: String, newName: NonBlankString)

}