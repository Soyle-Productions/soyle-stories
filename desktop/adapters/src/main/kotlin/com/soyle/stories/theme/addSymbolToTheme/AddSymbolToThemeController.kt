package com.soyle.stories.theme.addSymbolToTheme

import com.soyle.stories.domain.validation.NonBlankString

interface AddSymbolToThemeController {

    fun addSymbolToTheme(themeId: String, name: NonBlankString, onError: (Throwable) -> Unit)

}