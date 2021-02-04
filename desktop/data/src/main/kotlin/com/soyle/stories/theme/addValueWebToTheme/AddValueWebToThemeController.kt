package com.soyle.stories.theme.addValueWebToTheme

interface AddValueWebToThemeController {

    fun addValueWebToTheme(themeId: String, name: String, onError: (Throwable) -> Unit)
    fun addValueWebToThemeWithCharacter(themeId: String, name: String, characterId: String, onError: (Throwable) -> Unit)

}