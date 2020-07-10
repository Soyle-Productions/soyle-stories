package com.soyle.stories.theme.createSymbolDialog

data class CreateSymbolDialogViewModel(
    val title: String,
    val nameFieldLabel: String,
    val errorMessage: String?,
    val errorCause: String?,
    val themes: List<ThemeItemViewModel>,
    val createdId: String?
)

class ThemeItemViewModel(val themeId: String, val themeName: String)