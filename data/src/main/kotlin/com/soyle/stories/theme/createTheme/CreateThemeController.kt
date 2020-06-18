package com.soyle.stories.theme.createTheme

interface CreateThemeController {

    fun createTheme(name: String, onError: (Throwable) -> Unit)

}