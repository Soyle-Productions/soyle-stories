package com.soyle.stories.theme.addValueWebToTheme

import com.soyle.stories.domain.validation.NonBlankString

interface AddValueWebToThemeController {

    fun addValueWebToTheme(themeId: String, name: NonBlankString, onError: (Throwable) -> Unit)
    fun addValueWebToThemeWithCharacter(themeId: String, name: NonBlankString, characterId: String, onError: (Throwable) -> Unit)

}