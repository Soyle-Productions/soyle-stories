package com.soyle.stories.theme.createTheme

import com.soyle.stories.domain.validation.NonBlankString

interface CreateThemeController {

    fun createTheme(name: NonBlankString, onError: (Throwable) -> Unit)
    fun createThemeAndFirstSymbol(themeName: NonBlankString, symbolName: NonBlankString, onError: (Throwable) -> Unit)

}