package com.soyle.stories.theme.addSymbolToTheme

interface AddSymbolToThemeController {

    fun addSymbolToTheme(themeId: String, name: String, onError: (Throwable) -> Unit)

}